# 0007. API Gateway como punto de entrada unico

## Contexto

Hasta ahora, un cliente externo (Postman, o en el futuro un frontend) necesitaba conocer el puerto exacto de cada microservicio: 8081 para usuarios-service, 8082 para entrenamientos-service. Esto expone directamente la topologia interna del sistema al exterior, y complica el acceso cuando el numero de servicios crece (cada uno necesitaria su propio port-forward en Kubernetes, o su propia URL publica en un despliegue real).

## Decision

Se añade un tercer servicio, `gateway-service`, construido con Spring Cloud Gateway, que actua como unico punto de entrada del sistema (puerto 8080). Enruta las peticiones al microservicio correspondiente segun el path de la URL:

- `/usuarios/**` y `/perfiles/**` -> usuarios-service
- `/ejercicios/**`, `/entrenamientos/**`, `/entrenamiento-ejercicios/**`, `/registros/**`, `/prs/**` -> entrenamientos-service

A diferencia de usuarios-service y entrenamientos-service, el Gateway no tiene logica de negocio ni base de datos propia: su unica responsabilidad es enrutar.

## Alternativas consideradas

- **No usar Gateway y seguir exponiendo cada servicio por separado**: descartada porque no escala bien a medida que crece el numero de servicios, y obliga al cliente a conocer la topologia interna del sistema.
- **Meter la logica de enrutado dentro de uno de los microservicios existentes**: descartada porque rompe el principio de responsabilidad unica que se ha mantenido en el resto del proyecto, y crearia un acoplamiento innecesario entre usuarios-service y entrenamientos-service.

## Detalle tecnico: version de Spring Cloud Gateway usada

El Gateway se construyo con Spring Boot 4.1.1 (version distinta a la 3.5.16 que usan los otros dos servicios). Esto es intencionado y no supone un problema: cada microservicio es un proyecto Maven independiente que se comunica solo por HTTP, por lo que no necesitan compartir version de Spring Boot.

Con Spring Cloud Gateway en su version reciente (2025.1.3), el proyecto se dividio en dos variantes segun el modelo de programacion: `spring-cloud-starter-gateway-server-webflux` (reactivo) y su equivalente basado en Servlet/MVC. Esto cambio tambien el prefijo de configuracion de las rutas: ya no es `spring.cloud.gateway.routes` (el que aparece en la mayoria de tutoriales y documentacion antigua en internet), sino `spring.cloud.gateway.server.webflux.routes`. No usar el prefijo correcto no produce ningun error visible en el arranque, simplemente el Gateway arranca sin ninguna ruta cargada y responde 404 a todo, lo cual dificulta el diagnostico si no se sabe que buscar.

## Consecuencias

**Ventajas:**
- Un unico punto de entrada y un unico puerto que el cliente necesita conocer.
- Los microservicios individuales pueden considerarse "internos" a partir de ahora; su exposicion directa deja de ser necesaria.
- Prepara el terreno para añadir en el Gateway, en el futuro, preocupaciones transversales como autenticacion o rate limiting, sin tocar cada microservicio por separado.

**Limitaciones conocidas, aceptadas para el alcance actual:**
- Las rutas estan definidas de forma estatica en el `application.yml` del Gateway. Si se añadiera un tercer microservicio de negocio, habria que editar y redesplegar el Gateway. En un sistema mayor, esto se resolveria combinando el Gateway con service discovery.
- El Gateway no aplica todavia ninguna logica adicional (autenticacion, rate limiting, logging centralizado); por ahora es un enrutador puro.

## Despliegue en Kubernetes

gateway-service se desplego tambien en Kubernetes como un tercer Deployment y Service, siguiendo el mismo patron que usuarios-service y entrenamientos-service (imagen construida localmente con Dockerfile multi-stage, imagePullPolicy: Never).

La diferencia principal respecto al despliegue con Docker Compose esta en como el Gateway conoce las URLs de los otros dos servicios. En el `application.yml` del Gateway, las URLs se parametrizaron con la sintaxis de Spring `${VARIABLE:valor_por_defecto}`:

```yaml
uri: ${USUARIOS_SERVICE_URL:http://localhost:8081}
uri: ${ENTRENAMIENTOS_SERVICE_URL:http://localhost:8082}
```

Esto permite un unico artefacto (el mismo .jar y la misma imagen Docker) que se comporta correctamente en ambos entornos sin necesidad de mantener dos configuraciones separadas: en Docker Compose, al no definirse esas variables, se usa el valor por defecto (localhost); en Kubernetes, el Deployment inyecta esas variables apuntando a los nombres de los Service internos (`http://usuarios-service:8081`, `http://entrenamientos-service:8082`).

Con esto, exponer el sistema completo hacia fuera del cluster pasa de necesitar dos port-forward independientes (uno por microservicio) a necesitar solo uno, hacia el Service del Gateway:


Una peticion a traves de este unico puerto atraviesa tres Pods distintos (gateway-service, el microservicio de negocio correspondiente, y su base de datos), resueltos automaticamente por el DNS interno de Kubernetes en cada salto.