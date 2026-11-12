# Backlog de I-00

Flujo acordado: una tarea, su prueba o validacion correspondiente, commit y push
a `main` solo cuando la comprobacion pase. El codigo nuevo lleva comentarios
en espanol que explican responsabilidad, reglas y decisiones.

## I-00-01: base Maven y contrato de estados

- Estado: implementada; validacion y resultado en `docs/evidence/I-00-01.md`.
- Requisitos: secciones 5, 6 y 17; prepara RF-04, RF-10, RF-11 y RF-14.
- Archivos: POM raiz y de ambos servicios; `TicketStatus.java` y `TicketStatusTest.java`.
- Comportamiento: compilar con Java 21 y consultar las transiciones del grafo normativo.
- Prueba: 25 pares de estados y cinco destinos nulos con JUnit; `mvn -B verify`.
- Dependencia: especificacion versionada y Java/Maven instalados.
- Evidencia: `docs/evidence/I-00-01.md` e informes Surefire locales.
- Limite: no implementa permisos, persistencia, API, aprobacion ni procesamiento IA.

## I-00-02: decisiones de arquitectura

- Estado: completada; resultados en `docs/evidence/I-00-02.md`.
- Requisitos: secciones 6, 8, 9, 11, 12, 13 y 17; trazabilidad detallada por ADR.
- Archivos: indice y seis ADR en `docs/adr/`; evidencia en `docs/evidence/I-00-02.md`;
  validador comentado `scripts/validation/Test-ArchitectureDocs.ps1`.
- Comportamiento observable: responsabilidades, consistencia, RAG y aprobacion
  quedan documentados; los pendientes de identidad y proveedor real son explicitos.
- Validacion: seis ADR presentes, secciones obligatorias completas, enlaces locales
  validos, revision semantica contra invariantes y regresion `mvn -B verify`.
- Dependencia: I-00-01 completada.
- Limite: documentacion de diseno; no implementa los controles descritos.

## I-00-03: contrato REST OpenAPI

- Estado: completada; resultados en `docs/evidence/I-00-03.md`.
- Requisitos: seccion 7; prepara RF-01 a RF-04, RF-09 a RF-11, RF-13 y RF-14.
- Archivos: `contracts/openapi.yaml`, `contracts/README.md`, POM de tickets,
  `OpenApiContractTest.java` y evidencia.
- Comportamiento observable: once operaciones con esquemas, errores, restricciones
  y requisitos de identidad declarados; convenciones adicionales documentadas.
- Pruebas: parser OpenAPI y 25 casos contractuales; regresion de 30 casos de estados.
- Dependencia: I-00-02 completada.
- Limite: contrato de diseno; no implementa endpoints ni controles de seguridad.

## Siguientes tareas

| ID | Alcance y archivos previstos | Comportamiento / comprobacion | Dependencia |
|---|---|---|---|
| I-00-04 | Schemas y ejemplos en `contracts/events/` | Ejemplos validos aceptados e invalidos rechazados | I-00-03 |
| I-00-05 | Bootstrap Spring de los dos servicios | Arranque y health comprobados; versiones registradas | I-00-04 |
| I-00-06 | `infra/compose.yaml`, `.env.example` | Infraestructura inicia; health checks y smoke tests | I-00-05 |

Cada tarea siguiente detallara requisitos, archivos, casos y evidencia antes de iniciarse.
I-00 completo exige contratos coherentes y entorno funcionando; I-00-01 no satisface esa puerta por si solo.
