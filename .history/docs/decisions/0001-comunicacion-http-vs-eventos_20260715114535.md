# 0001. Comunicación HTTP síncrona en lugar de eventos

## Contexto

`entrenamientos-service` necesita saber si un usuario existe en `usuarios-service` antes de crear un `Registro` o un `PR`, ya que ambas entidades referencian a un usuario mediante un `usuarioId` que vive en otra base de datos. Al tratarse de dos bases de datos independientes, no es posible validar esa referencia con una consulta SQL ni con una clave foránea real.

## Decisión

Opté por una llamada HTTP síncrona (`RestClient`) desde `entrenamientos-service` hacia el endpoint `GET /usuarios/{id}/existe` de `usuarios-service`, esperando la respuesta antes de continuar con la operación.

## Alternativas consideradas

- **Comunicación asíncrona con eventos (Kafka o RabbitMQ):** descartada para esta fase del proyecto. Tiene sentido cuando varios servicios necesitan reaccionar a un mismo hecho de forma desacoplada, o cuando la consistencia inmediata no es crítica (por ejemplo, "un usuario se dio de baja, notifica a todos los servicios interesados"). En este caso, la operación necesita saber sí o sí, en el momento, si el usuario existe antes de guardar el registro; no tiene sentido crear el registro y corregirlo después de forma eventual.
- **Duplicar los datos del usuario en `entrenamientos_db`:** descartada porque rompería el principio de que cada servicio es la única fuente de verdad de sus propios datos, y generaría un problema de sincronización cada vez que un usuario cambiara.

## Consecuencias

- **Ventaja:** el flujo es fácil de razonar, depurar y desplegar. No hay que gestionar un broker de mensajería adicional ni la complejidad operativa que conlleva (colas, particiones, garantías de entrega).
- **Desventaja:** se introduce acoplamiento temporal entre los dos servicios. Si `usuarios-service` no responde, `entrenamientos-service` no puede completar la operación (ver [0003](0003-manejo-de-fallos-usuarios-service.md)).
- **Desventaja:** cada llamada añade latencia de red donde antes, en un monolito, habría sido una consulta SQL instantánea.