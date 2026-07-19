# 0006. Orquestación con Kubernetes

## Contexto

Con Docker Compose, el sistema ya podía desplegarse de forma reproducible, pero seguían existiendo algunas limitaciones ya recogidas en el [ADR 0004](0004-evolucion-futura-de-la-arquitectura.md): cada servicio dependía de la URL fija de los demás, no había recuperación automática si un contenedor fallaba y tampoco existía un mecanismo real de *service discovery*.

Además de resolver parte de esas limitaciones, la principal motivación para incorporar Kubernetes fue de aprendizaje. Es una tecnología muy presente en ofertas de desarrollo backend y DevOps, y este proyecto, con dos microservicios independientes y sus propias bases de datos, ofrecía un escenario más cercano a un caso real que el típico ejemplo de un único contenedor.

## Decisión

Se despliega el sistema completo en un clúster local de Kubernetes (Minikube), trasladando cada uno de los componentes definidos en `docker-compose.yml` a sus recursos equivalentes en Kubernetes.

| Concepto en Docker Compose | Equivalente en Kubernetes |
|---|---|
| `services.mysql-usuarios` | `Deployment` |
| `volumes:` | `PersistentVolumeClaim` |
| Nombre de contenedor como hostname | `Service` (DNS interno) |
| `environment:` con contraseñas | `Secret` |
| `environment:` con configuración no sensible | `ConfigMap` |

Cada microservicio y cada base de datos dispone de su propio `Deployment` y `Service`.

Las imágenes de los microservicios se construyen mediante un `Dockerfile` multi-stage: una primera etapa utiliza Maven para compilar la aplicación y una segunda ejecuta únicamente el JAR sobre un JRE, reduciendo el tamaño final de la imagen. Una vez construidas, las imágenes se cargan directamente en el motor Docker de Minikube, sin necesidad de publicarlas previamente en un registro.

## Cómo resuelve el *service discovery* pendiente en el ADR 0004

En Docker Compose, `entrenamientos-service` accedía a `usuarios-service` utilizando el nombre del contenedor. Kubernetes mantiene esa idea, pero de una forma más robusta.

El `Service` de `usuarios-service` proporciona un nombre DNS estable (`usuarios-service`) que siempre apunta al Pod disponible en ese momento. Si el Pod se reinicia o Kubernetes lo mueve a otro nodo, el nombre sigue siendo el mismo y la resolución se actualiza automáticamente. De esta forma desaparece la dependencia de una IP concreta o de un contenedor específico.

La URL se inyecta mediante un `ConfigMap` (`USUARIOS_SERVICE_URL=http://usuarios-service:8081`). Spring Boot la convierte automáticamente en la propiedad `usuarios-service.url` gracias a su mecanismo de configuración mediante variables de entorno, por lo que no ha sido necesario modificar el código ni el `application.properties`.

## Consecuencias

### Ventajas

- Si un Pod deja de estar disponible, el `Deployment` crea automáticamente otro para mantener el estado deseado.
- Escalar un microservicio consiste simplemente en aumentar el número de réplicas del `Deployment` (las bases de datos no se escalan por los motivos explicados en ADR anteriores).
- La configuración depende del entorno mediante variables de entorno, por lo que el mismo JAR puede ejecutarse tanto con Docker Compose como con Kubernetes sin necesidad de recompilar la aplicación.

### Limitaciones aceptadas para el alcance del proyecto

- Las credenciales de MySQL se almacenan en un `Secret`, pero permanecen codificadas en Base64 dentro del repositorio. Esto es suficiente para un entorno de aprendizaje, aunque no sería una solución válida en producción. En un despliegue real se utilizaría un gestor de secretos como Vault, Sealed Secrets o el servicio equivalente del proveedor cloud.
- El acceso a los servicios se realiza mediante `kubectl port-forward`, una solución pensada para desarrollo local. En un entorno real sería necesario utilizar un `Ingress` o un `Service` de tipo `LoadBalancer`.
- No se han definido límites ni reservas de CPU y memoria (`resources.requests` y `resources.limits`), por lo que el clúster no podría controlar el consumo de recursos de cada Pod.
- El objetivo de esta fase es aprender los conceptos fundamentales de Kubernetes (Deployment, Service, ConfigMap, Secret y PersistentVolumeClaim). Docker Compose sigue formando parte del proyecto como la forma más rápida de levantar el entorno durante el desarrollo.