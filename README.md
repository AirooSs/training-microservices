## Arquitectura

El sistema está compuesto por dos microservicios independientes, cada uno con su propia base de datos MySQL, que se comunican entre sí mediante HTTP (RestClient):

```mermaid
flowchart TB
    Cliente["Cliente (Postman)"]
    US["usuarios-service<br/>puerto 8081"]
    ES["entrenamientos-service<br/>puerto 8082"]
    DBU[("MySQL usuarios_db<br/>puerto 3307")]
    DBE[("MySQL entrenamientos_db<br/>puerto 3308")]

    Cliente --> US
    Cliente --> ES
    ES -- HTTP --> US
    US --> DBU
    ES --> DBE
```

`entrenamientos-service` valida contra `usuarios-service` que un usuario existe antes de crear un registro de entrenamiento o un récord personal (PR). Cada base de datos es completamente independiente: no hay claves foráneas entre servicios, solo referencias por id validadas vía HTTP.

El sistema puede desplegarse de dos formas: con Docker Compose (más simple, pensado para desarrollo rápido) o con Kubernetes (más cercano a un entorno real, con recuperación automática de Pods y service discovery nativo).

## Decisiones de diseño

- Una base de datos MySQL independiente por microservicio, sin acceso cruzado.
- Comunicación síncrona mediante HTTP en lugar de eventos.
- Validación del usuario contra usuarios-service antes de crear cualquier recurso que dependa de él.
- Los campos usuarioId en Registro y PR son referencias sueltas (Long), no claves foráneas.
- DTOs específicos por endpoint en lugar de exponer las entidades JPA directamente en la API.
- Manejo de errores centralizado, distinguiendo entre "recurso no encontrado" (404) y "servicio externo no disponible" (503).
- Orquestación con Kubernetes como alternativa a Docker Compose, resolviendo de forma nativa el service discovery entre microservicios.

El razonamiento completo detrás de cada decisión (contexto, alternativas consideradas y consecuencias) está documentado como Architecture Decision Records en [docs/decisions/](docs/decisions/):

- [0001. Comunicación HTTP síncrona en lugar de eventos](docs/decisions/0001-comunicacion-http-vs-eventos.md)
- [0002. Una base de datos independiente por microservicio](docs/decisions/0002-base-de-datos-por-servicio.md)
- [0003. Qué ocurre si usuarios-service deja de estar disponible](docs/decisions/0003-manejo-de-fallos-usuarios-service.md)
- [0004. Cómo evolucionaría la arquitectura si el sistema creciera](docs/decisions/0004-evolucion-futura-de-la-arquitectura.md)
- [0005. Estrategia de testing: Testcontainers y aislamiento de contexto](docs/decisions/0005-estrategia-de-testing.md)
- [0006. Orquestación con Kubernetes](docs/decisions/0006-orquestacion-con-kubernetes.md)

## Stack tecnológico

- Java 21
- Spring Boot 3.5.16
- Spring Data JPA / Hibernate
- Spring Validation
- RestClient (comunicación entre microservicios)
- MySQL 8
- Docker / Docker Compose
- Kubernetes (Minikube)
- Maven
- Lombok
- Testcontainers (tests de integración con base de datos real)
- OkHttp MockWebServer (simulación de usuarios-service en tests)

## Servicios

### usuarios-service (puerto 8081)

Gestiona los usuarios y su perfil físico (peso, altura, histórico).

| Método | Endpoint | Descripción |
|---|---|---|
| POST | /usuarios | Crea un usuario |
| GET | /usuarios/{id} | Consulta un usuario |
| GET | /usuarios | Lista todos los usuarios |
| PUT | /usuarios/{id} | Actualiza un usuario |
| DELETE | /usuarios/{id} | Elimina un usuario |
| GET | /usuarios/{id}/existe | Comprueba si un usuario existe (uso interno, consumido por entrenamientos-service) |
| POST | /perfiles | Crea un registro de perfil físico |
| GET | /perfiles/usuario/{usuarioId} | Historial de perfil físico de un usuario |

### entrenamientos-service (puerto 8082)

Gestiona ejercicios, entrenamientos, registros de entrenamiento y récords personales (PR).

| Método | Endpoint | Descripción |
|---|---|---|
| POST | /ejercicios | Crea un ejercicio |
| GET | /ejercicios/{id} | Consulta un ejercicio |
| GET | /ejercicios | Lista todos los ejercicios |
| POST | /entrenamientos | Crea un entrenamiento |
| GET | /entrenamientos/{id} | Consulta un entrenamiento |
| GET | /entrenamientos | Lista todos los entrenamientos |
| GET | /entrenamientos/hoy | Entrenamientos programados para hoy |
| POST | /entrenamiento-ejercicios | Añade un ejercicio a un entrenamiento |
| GET | /entrenamiento-ejercicios/entrenamiento/{id} | Ejercicios de un entrenamiento |
| POST | /registros | Crea un registro de entrenamiento (valida el usuario vía HTTP) |
| GET | /registros/usuario/{usuarioId} | Historial de entrenamientos de un usuario |
| POST | /prs | Registra un nuevo récord personal (solo si supera el anterior) |
| GET | /prs/usuario/{usuarioId} | Evolución de récords personales de un usuario |

Nota: Se expone un endpoint específico para comprobar la existencia de un usuario y evitar acoplar el servicio consumidor a la representación completa del recurso. De este modo, entrenamientos-service solo necesita conocer si el usuario existe, no sus datos.

## Cómo levantarlo en local

### Opción A: Docker Compose (más simple, recomendado para desarrollo)

Requisitos: Docker Desktop, Java 21, Maven (o el wrapper incluido mvnw)

1. Clona el repositorio:

git clone https://github.com/AirooSs/training-microservices.git
cd training-microservices

2. Levanta las bases de datos MySQL con Docker Compose:

docker compose up -d

3. Arranca usuarios-service (puerto 8081):

cd usuarios-service
./mvnw spring-boot:run

4. En otra terminal, arranca entrenamientos-service (puerto 8082):

cd entrenamientos-service
./mvnw spring-boot:run

5. Ambos servicios generan sus tablas automáticamente mediante spring.jpa.hibernate.ddl-auto=update, una configuración adecuada para desarrollo. En entornos de producción sería recomendable utilizar herramientas de migración como Flyway o Liquibase.

### Opción B: Kubernetes (con Minikube)

Requisitos adicionales: kubectl, Minikube

1. Arranca el clúster local:

minikube start --driver=docker

2. Apunta tu terminal al Docker interno de Minikube (necesario para que el clúster vea las imágenes que construyas):

minikube docker-env | Invoke-Expression

3. Construye las imágenes de ambos servicios:

cd usuarios-service
docker build -t usuarios-service:1.0 .
cd ../entrenamientos-service
docker build -t entrenamientos-service:1.0 .
cd ..

4. Aplica todos los manifiestos:

kubectl apply -f k8s/

5. Comprueba que los 4 Pods están en estado Running:

kubectl get pods

6. Expón los servicios para probarlos desde fuera del clúster (en dos terminales distintas):

kubectl port-forward service/usuarios-service 8081:8081
kubectl port-forward service/entrenamientos-service 8082:8082

El razonamiento completo de esta implementación (equivalencias con Docker Compose, cómo se resuelve el service discovery, y las limitaciones conocidas de este despliegue) está documentado en el [ADR 0006](docs/decisions/0006-orquestacion-con-kubernetes.md).

## Modelo de datos

El modelo se divide en dos bases de datos independientes:

usuarios_db
- Usuario: datos básicos del usuario
- PerfilFisico: histórico de peso y altura (relación 1:N con Usuario)

entrenamientos_db
- Ejercicio: catálogo de ejercicios (clasificados por patrón de movimiento: empuje, tracción, pierna)
- Entrenamiento: sesiones de entrenamiento (fuerza, hipertrofia o cardio)
- EntrenamientoEjercicio: tabla intermedia que resuelve la relación N:M entre Entrenamiento y Ejercicio
- Registro: resultado de un usuario en un entrenamiento concreto
- PR: récord personal de un usuario en un ejercicio (peso máximo levantado)

## Testing

Ambos servicios cuentan con tests de integración automatizados usando Testcontainers, que levantan un contenedor MySQL real (no una base de datos en memoria) para cada ejecución:

- usuarios-service: creación de usuario, validación de email duplicado, consulta de usuario inexistente (404).
- entrenamientos-service: creación de un registro de entrenamiento con validación cruzada real contra un usuarios-service simulado (MockWebServer), cubriendo los tres escenarios: usuario existente (201), usuario inexistente (404), y usuarios-service no disponible (503).

El último escenario prueba de forma automatizada el comportamiento de resiliencia descrito en el [ADR 0003](docs/decisions/0003-manejo-de-fallos-usuarios-service.md).

Además, se ha probado manualmente de extremo a extremo con Postman en ambos entornos de despliegue (Docker Compose y Kubernetes): creación de ejercicios y entrenamientos, comunicación real entre Pods a través del Service de Kubernetes, y lógica de negocio de récords personales (rechazo de un peso que no supera el récord actual).

## Lo aprendido

Durante este proyecto he practicado:

- Diseño y desarrollo de microservicios con Spring Boot
- Comunicación HTTP síncrona entre servicios con RestClient
- Diseño de bases de datos independientes por dominio
- Validaciones cruzadas entre servicios
- JPA / Hibernate, incluyendo relaciones N:M con tabla intermedia
- Manejo de errores centralizado en una API REST
- Uso de DTOs para desacoplar la API del modelo de datos interno
- Orquestación de contenedores con Docker Compose
- Tests de integración con Testcontainers y simulación de servicios externos con MockWebServer
- Orquestación con Kubernetes: Deployments, Services, PersistentVolumeClaims, Secrets y ConfigMaps
- Construcción de imágenes Docker multi-stage para aplicaciones Spring Boot
- Configuración externalizada de Spring Boot mediante variables de entorno según el entorno de despliegue

## Roadmap (lo no marcado son posibles implementaciones futuras)

- [x] Comunicación HTTP entre microservicios
- [x] Docker Compose con bases de datos independientes
- [x] Manejo de errores centralizado
- [x] Tests de integración con Testcontainers
- [x] Orquestación con Kubernetes
- [ ] Documentación OpenAPI / Swagger
- [ ] Spring Cloud Gateway
- [ ] Autenticación JWT
- [ ] Comunicación asíncrona con eventos (Kafka o RabbitMQ)
- [ ] Gestión de secretos con una herramienta dedicada (Sealed Secrets o similar)

## Autor

Francisco José Soria Navarrete
[LinkedIn](https://linkedin.com/in/fran-soria-nav) · [GitHub](https://github.com/AirooSs)