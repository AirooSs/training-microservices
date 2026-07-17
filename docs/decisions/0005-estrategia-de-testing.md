# 0005. Estrategia de testing: Testcontainers y aislamiento de contexto

## Contexto

Al incorporar tests de integración automatizados, había que decidir cómo probar el acceso a base de datos (¿base de datos real o en memoria?) y cómo estructurar los tests que dependen de la comunicación HTTP entre `entrenamientos-service` y `usuarios-service`.

## Decisión: utilizar Testcontainers en lugar de H2

Se utiliza Testcontainers para levantar un contenedor MySQL real durante la ejecución de los tests de integración, en lugar de emplear una base de datos en memoria como H2.

### Alternativa considerada

Se valoró utilizar H2 en modo de compatibilidad con MySQL. Esta opción se descartó porque, aunque reduce el tiempo de arranque de los tests, no reproduce completamente el comportamiento de MySQL. Existen diferencias en el dialecto SQL, el tratamiento de determinados tipos de datos (como `ENUM`, utilizado en varias entidades del proyecto) y en algunas restricciones y validaciones de la base de datos.

Como consecuencia, un test que supera H2 podría fallar al ejecutarse sobre MySQL en un entorno real, reduciendo la fiabilidad de la batería de pruebas.

### Consecuencias

- Mayor fidelidad respecto al entorno de producción.
- Mayor confianza en los tests de persistencia.
- Incremento del tiempo de ejecución debido al arranque del contenedor MySQL.

---

## Decisión: aislar el escenario de "servicio caído" en un contexto independiente

Los tests de integración del registro utilizan un `MockWebServer` para simular las respuestas de `usuarios-service`.

Durante el desarrollo se planteó reutilizar el mismo servidor para comprobar el escenario en el que `usuarios-service` no está disponible, apagándolo (`shutdown()`) dentro del propio test.

### Problema detectado

Al detener el servidor compartido, este deja de estar disponible para el resto de tests de la clase. Dado que JUnit no garantiza el orden de ejecución, esto podía provocar fallos intermitentes y dependencias implícitas entre pruebas.

### Decisión

El escenario de servicio no disponible se trasladó a una clase de integración independiente (`ServicioCaidoIT`).

Esta clase arranca su propio contexto de Spring y configura `usuarios-service.url` para apuntar, desde el inicio del test, a un puerto donde no existe ningún servicio escuchando, simulando así una indisponibilidad real sin modificar el estado compartido por otros tests.

### Consecuencias

- Los tests son independientes entre sí.
- Se elimina el riesgo de dependencias ocultas entre pruebas.
- El comportamiento de la suite es determinista independientemente del orden de ejecución.
- Aumenta ligeramente el tiempo total de ejecución al iniciarse un contexto adicional de Spring.