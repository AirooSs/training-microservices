# 0001. Comunicación HTTP síncrona en lugar de eventos

## Contexto

`entrenamientos-service` necesita validar la existencia de un usuario en `usuarios-service` antes de crear un `Registro` o un `PR`, ya que ambas entidades referencian un `usuarioId` cuyo ciclo de vida pertenece a otro microservicio. Al existir bases de datos independientes, esta referencia no puede garantizarse mediante claves foráneas ni consultas SQL entre servicios.

## Decisión

Opté por una llamada HTTP síncrona mediante `RestClient` desde `entrenamientos-service` al endpoint `GET /usuarios/{id}/existe` de `usuarios-service`, bloqueando la operación hasta obtener la respuesta. La creación del recurso solo continúa cuando la existencia del usuario ha sido validada.

## Alternativas consideradas

- **Comunicación asíncrona con eventos (Kafka o RabbitMQ):** descartada para esta fase del proyecto. Tiene sentido cuando varios servicios necesitan reaccionar a un mismo hecho de forma desacoplada, o cuando la consistencia inmediata no es crítica (por ejemplo, "un usuario se dio de baja, notifica a todos los servicios interesados"). En este caso, la consistencia eventual no satisface el requisito funcional de validar la existencia del usuario antes de persistir el registro.
- **Duplicar los datos del usuario en `entrenamientos_db`:** descartada porque rompería el principio de Single Source of Truth, ya que `usuarios-service` dejaría de ser el único propietario de la información del usuario y aparecerían problemas de sincronización y consistencia.

## Consecuencias

- **Ventaja:** el flujo mantiene consistencia fuerte durante la operación y resulta sencillo de implementar, depurar y operar. No hay que gestionar un broker de mensajería adicional ni la complejidad operativa que conlleva (colas, particiones, garantías de entrega).
- **Desventaja:** se introduce acoplamiento temporal entre los dos servicios. Si `usuarios-service` no responde, `entrenamientos-service` no puede completar la operación (ver [0003](0003-manejo-de-fallos-usuarios-service.md)).
- **Desventaja:** cada operación incorpora una llamada de red adicional, aumentando la latencia respecto a una consulta local en un monolito.