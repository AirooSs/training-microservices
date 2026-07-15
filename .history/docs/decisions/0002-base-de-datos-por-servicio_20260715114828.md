# 0002. Una base de datos independiente por microservicio

## Contexto

El proyecto está dividido en `usuarios-service` y `entrenamientos-service`. Había que decidir si compartirían una única base de datos MySQL (con esquemas o tablas separadas) o si cada uno tendría su propia instancia de base de datos completamente aislada.

## Decisión

Cada microservicio tiene su propia base de datos MySQL, en un contenedor Docker independiente (`usuarios_db` en el puerto 3307, `entrenamientos_db` en el puerto 3308), sin acceso cruzado entre ellas.

## Alternativas consideradas

- **Base de datos compartida con esquemas separados:** descartada porque, aunque físicamente estarían aisladas por esquema, seguiría siendo tentador (y técnicamente posible) hacer un `JOIN` directo entre tablas de distintos dominios, lo que en la práctica recrea el acoplamiento de un monolito y anula gran parte del sentido de usar microservicios.
- **Una única base de datos sin separación:** descartada por el mismo motivo, de forma aún más directa.

## Consecuencias

**Ventajas:**
- Cada servicio puede evolucionar su esquema de forma independiente, sin coordinar cambios con el otro equipo (o, en este caso, sin tener que pensar en el otro servicio al modificar una tabla).
- Cada base de datos podría escalarse o incluso cambiar de motor de forma independiente en el futuro, sin afectar al otro servicio.
- Refuerza el límite de responsabilidad de cada servicio: `usuarios-service` es la única fuente de verdad sobre los usuarios, `entrenamientos-service` lo es sobre los entrenamientos.

**Trade-offs:**
- Se pierde la posibilidad de hacer `JOIN` entre entidades de distintos servicios. El campo `usuarioId` en `Registro` y `PR` es una referencia suelta (`Long`), no una clave foránea real.
- La integridad referencial entre servicios ya no la garantiza la base de datos, sino la lógica de aplicación (la llamada HTTP de validación, ver [0001](0001-comunicacion-http-vs-eventos.md)).
- Aparecen preguntas nuevas que en un monolito no existirían, como qué hacer si el servicio del que depende una validación no está disponible (ver [0003](0003-manejo-de-fallos-usuarios-service.md)).