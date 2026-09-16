# 0008. Autenticacion con JWT centralizada en el Gateway

## Contexto

Hasta ahora, cualquier cliente podia acceder a cualquier endpoint de ambos microservicios sin ningun tipo de autenticacion. Se necesitaba un mecanismo para identificar quien hace cada peticion y restringir el acceso a usuarios autenticados.

## Decision

Se implemento autenticacion basada en JWT (JSON Web Tokens), con el siguiente reparto de responsabilidades:

- `usuarios-service` es el unico responsable de gestionar credenciales: expone `POST /auth/login`, que valida email y contrasena (hasheada con BCrypt, nunca en texto plano) y devuelve un token JWT firmado con expiracion de 1 hora.
- `gateway-service` es el unico responsable de validar el token en cada peticion entrante, mediante un `GlobalFilter` que intercepta todo el trafico antes de que se redirija a cualquier microservicio. Si el token falta, esta mal formado, la firma no es valida o expiro, el Gateway rechaza la peticion con 401 sin que llegue a usuarios-service ni entrenamientos-service.
- Las unicas rutas publicas (sin exigir token) son `POST /usuarios` (registro) y `POST /auth/login`.

## Alternativas consideradas

- **Validar el token en cada microservicio por separado** (usuarios-service y entrenamientos-service, cada uno con su propio filtro de seguridad): descartada porque duplicaria la logica de validacion en dos sitios, y cualquier cambio futuro (rotar la clave, cambiar el algoritmo de firma) tendria que aplicarse en varios lugares a la vez. Centralizarlo en el Gateway, que ya actua como punto de entrada unico (ver [0007](0007-api-gateway.md)), evita esta duplicacion: es exactamente el tipo de preocupacion transversal que se anticipo como ventaja futura del Gateway.
- **Sesiones con estado en servidor en lugar de JWT**: descartada porque rompe con el principio de que cada microservicio (y el propio Gateway) no deberia mantener estado entre peticiones, dificultando el escalado horizontal. Un JWT es autocontenido: cualquier instancia del Gateway puede validarlo sin necesitar consultar una base de datos de sesiones compartida.

## Consecuencias

**Ventajas:**
- Autenticacion centralizada en un unico punto (el Gateway), sin logica de seguridad duplicada entre microservicios.
- El sistema permanece sin estado (stateless): ninguna peticion depende de una sesion guardada en servidor.
- usuarios-service y gateway-service comparten la misma clave de firma, pero solo usuarios-service necesita generar tokens; gateway-service solo necesita verificarlos.

**Limitaciones conocidas, aceptadas para el alcance actual:**

- **entrenamientos-service no tiene ninguna proteccion propia.** El filtro de JWT vive unicamente en el Gateway. Si un cliente accede directamente al puerto de entrenamientos-service (saltandose el Gateway), puede usar toda su API sin ningun token. Esto es coherente con la idea de que los microservicios de negocio deberian considerarse "internos" (ver [0007](0007-api-gateway.md)), pero en el despliegue actual con Docker Compose no hay ninguna barrera de red real que impida ese acceso directo; el puerto 8082 sigue expuesto en la maquina local. En el despliegue de Kubernetes esto esta parcialmente mitigado porque los Services de los microservicios de negocio no se exponen hacia fuera del cluster salvo con un port-forward explicito, pero tampoco existe una politica de red (NetworkPolicy) que lo impida a nivel de infraestructura.
- **La clave secreta (`jwt.secret`) esta en texto plano en el `application.properties`/`application.yml` de ambos servicios, y debe coincidir exactamente entre ellos.** Esta es la misma limitacion que ya se documento para las credenciales de MySQL en Kubernetes (ver [0006](0006-orquestacion-con-kubernetes.md)): en un entorno real se gestionaria con un almacen de secretos dedicado, no como texto plano en el repositorio.
- **No hay revocacion de tokens.** Si un token se filtrara, seguiria siendo valido hasta su expiracion natural (1 hora), sin ninguna forma de invalidarlo antes. Una lista negra de tokens revocados, o un mecanismo de refresh tokens con invalidacion, quedan fuera del alcance actual.
- **No existe todavia un sistema de roles o permisos.** El token confirma que un usuario esta autenticado, pero no distingue niveles de acceso: cualquier usuario autenticado puede operar sobre cualquier recurso, sin restriccion por rol.