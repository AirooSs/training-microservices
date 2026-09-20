# 0009. Restriccion de acceso a entrenamientos-service con NetworkPolicy

## Contexto

El [ADR 0008](0008-autenticacion-jwt.md) documenta que la autenticacion JWT se valida unicamente en el Gateway, y que entrenamientos-service no tiene ninguna proteccion propia: cualquiera con acceso de red directo al Pod puede usar su API sin pasar por el Gateway ni presentar un token. Se necesitaba una forma de cerrar ese acceso directo a nivel de infraestructura, sin tener que duplicar la logica de autenticacion dentro del propio microservicio.

## Decision

Se creo una `NetworkPolicy` de Kubernetes (`k8s/07-networkpolicy-entrenamientos.yaml`) que restringe el trafico entrante a los Pods de entrenamientos-service, permitiendo unicamente conexiones que provengan de Pods con la etiqueta `app: gateway-service`, en el puerto 8082. Cualquier otro origen deberia quedar bloqueado.

## Verificacion y limitacion encontrada

Se probo la politica de dos formas:

1. Con `kubectl port-forward` directo al Service de entrenamientos-service: esta prueba no es representativa, ya que el port-forward de kubectl no atraviesa el mismo camino de red que el trafico entre Pods.
2. De forma mas realista, lanzando un Pod temporal (`kubectl run test-intruso --image=curlimages/curl ...`) que no tiene la etiqueta `app: gateway-service`, e intentando acceder a `http://entrenamientos-service:8082/entrenamientos` desde dentro de ese Pod. Esta peticion **se completo con exito (200 OK)**, cuando deberia haber sido bloqueada.

Se investigo la causa: `kubectl get pods -n kube-system` confirmo que el cluster de Minikube no tiene ningun plugin de red (CNI) que soporte `NetworkPolicy` instalado (ni Calico ni ningun otro). El driver por defecto de Minikube con Docker acepta el recurso `NetworkPolicy` en la API de Kubernetes (`kubectl apply` lo crea sin error), pero **no lo hace cumplir realmente**, porque el cumplimiento de estas politicas depende del plugin de red del cluster, no del propio recurso en si.

Se intento instalar Calico sobre el cluster existente (`minikube start --cni=calico`), pero al ser un cluster ya creado sin CNI desde el inicio, el cambio no se aplico (no aparecio ningun Pod de Calico en `kube-system` tras el reinicio). Recrear el cluster desde cero con Calico (`minikube delete` + `minikube start --cni=calico`) habria sido necesario para probarlo correctamente, pero se decidio no hacerlo en esta fase por el riesgo de invertir tiempo desproporcionado en un problema de entorno local que no refleja como se comportaria en un cluster real.

## Consecuencias

- El manifiesto `NetworkPolicy` es correcto y esta versionado en el repositorio; en un cluster con un CNI compatible (Calico, Cilium, o el CNI por defecto de un proveedor cloud como EKS...) se aplicaria y haria cumplir sin necesidad de ningun cambio.

- En el entorno actual de desarrollo local (Minikube sin CNI adicional), la politica **no ofrece proteccion real**: entrenamientos-service sigue siendo accesible directamente desde cualquier Pod del cluster sin pasar por el Gateway. Esta limitacion queda documentada explicitamente para no dar una falsa sensacion de seguridad.

- Si el proyecto avanzara hacia un entorno mas cercano a produccion, el siguiente paso seria recrear el cluster local con Calico habilitado desde el inicio, o migrar las pruebas de este comportamiento a un cluster gestionado que ya incluya un CNI compatible por defecto.