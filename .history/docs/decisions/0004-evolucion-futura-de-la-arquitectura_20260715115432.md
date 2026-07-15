# 0004. Cómo evolucionaría la arquitectura si el sistema creciera

## Contexto

El sistema actual tiene solo dos microservicios, que se llaman directamente entre sí por HTTP conociendo la URL exacta del otro (configurada en `application.properties`). Este documento recoge cómo debería evolucionar el diseño si el número de servicios o el volumen de tráfico creciera significativamente, y por qué las decisiones actuales no se han implementado ya (para no añadir complejidad que este proyecto, en su estado actual, no necesita).

## Limitaciones del diseño actual a partir de cierta escala

Con más de dos o tres servicios, que cada uno llame directamente a los demás conociendo su URL se vuelve difícil de mantener: cada servicio necesitaría una lista de URLs de todos los servicios de los que depende, y esas URLs cambiarían entre entornos (local, staging, producción) y cada vez que se despliega una nueva instancia.

## Evolución propuesta

**Service discovery (Eureka o Consul).** En lugar de que cada servicio conozca URLs fijas, se registrarían a sí mismos en un servidor de descubrimiento, y se localizarían entre ellos por nombre lógico, no por IP/puerto. Esto también permitiría tener varias instancias de un mismo servicio sin reconfigurar nada manualmente.

**API Gateway (Spring Cloud Gateway).** Un único punto de entrada para los clientes externos, que enrutaría las peticiones al servicio correspondiente. Esto evita exponer cada microservicio directamente, centraliza preocupaciones transversales (autenticación, rate limiting, logging) y simplifica el CORS y la gestión de certificados.

**Revisión de qué comunicaciones deberían pasar de síncronas a asíncronas.** No todas las llamadas HTTP actuales necesitarían seguir siendo síncronas si el sistema creciera. Por ejemplo, si en el futuro se añadiera un servicio de notificaciones que avisa a un usuario cuando bate un récord personal, ese caso encajaría mejor con un evento ("PR registrado") que otros servicios puedan consumir de forma desacoplada, en lugar de que `entrenamientos-service` tuviera que llamar directamente al servicio de notificaciones y esperar su respuesta.

**Autenticación centralizada con JWT**, validado en el API Gateway o en cada servicio, en lugar del acceso completamente abierto actual.

## Por qué no se ha implementado ya

Con solo dos servicios y un volumen de tráfico de desarrollo/portfolio, introducir Eureka, un Gateway y mensajería añadiría complejidad operativa (más contenedores, más puntos de fallo, más curva de aprendizaje). Se ha priorizado tener un sistema simple, correcto y bien entendido de principio a fin, dejando estas piezas documentadas como evolución natural cuando el contexto las justifique, en lugar de añadirlas de forma prematura solo por completitud técnica. Conforme este proyecto avance se irán implementando mejoras que por ahora son innecesarias dada la complejidad que introduciríamos.