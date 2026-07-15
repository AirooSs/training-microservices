# 0003. ¿Qué ocurre si usuarios-service deja de estar disponible?

## Contexto

Como `entrenamientos-service` depende de una llamada HTTP a `usuarios-service` para validar usuarios
(ver [0001](0001-comunicacion-http-vs-eventos.md)), había que decidir qué comportamiento tendría el sistema si esa llamada fallara: timeout, conexión rechazada, o cualquier otro error de red.

## Decisión

Se creó una excepción específica, `ServicioExternoNoDisponibleException`, distinta de `RecursoNoEncontradoException`. Cuando la llamada HTTP a `usuarios-service` falla por un problema de comunicación (no porque el usuario no exista, sino porque no se pudo ni preguntar), `entrenamientos-service` devuelve un `503 Service Unavailable` con un mensaje explícito, en lugar de un error genérico 500 o un fallo silencioso.

La distinción entre las dos excepciones es intencionada: `RecursoNoEncontradoException` (404) significa "pregunté y la respuesta fue que no existe"; `ServicioExternoNoDisponibleException` (503) significa "no pude ni preguntar". Son situaciones distintas que un cliente de la API debería poder distinguir (la primera es un error del cliente, la segunda es un problema temporal del sistema).

## Alternativas consideradas

- **Dejar que la excepción técnica de HTTP subiera sin capturar:** descartada porque expondría detalles internos (stacktraces de `RestClientException`) al cliente de la API, y no daría un código de estado semánticamente correcto.
- **Devolver `null` o `false` sin distinguir el motivo:** descartada porque haría indistinguible "el usuario no existe" de "no se pudo comprobar", lo cual podría llevar a decisiones incorrectas en el código que consume el resultado.

## Consecuencias

- **Ventaja:** el cliente de la API recibe información clara y accionable sobre qué ha ocurrido.
- **Limitación actual (pendiente en el roadmap):** no existe todavía ningún mecanismo más allá de devolver el error. No hay reintentos con backoff, ni circuit breaker, ni ningún tipo de caché de la última validación conocida. Si `usuarios-service` está caído, cada intento de crear un registro fallará inmediatamente, y en un escenario de alta carga, todas esas peticiones fallidas podrían añadir presión innecesaria sobre un servicio que ya está teniendo problemas.
- **Próximo paso natural:** incorporar Resilience4j para añadir reintentos (ya sea con backoff exponencial y un circuit breaker que deje de intentar llamar a `usuarios-service` durante un tiempo si se detecta que está caído, en lugar de seguir golpeándolo con peticiones.