# ADR-0002: Outbox, inbox y entrega al menos una vez

- Estado: aceptado.
- Fecha: 2026-11-11.
- Trazabilidad: RF-01, RF-05, RF-12, RF-15; INV-02, INV-03, INV-04; secciones 8, 9 y 13.

## Contexto

PostgreSQL y Kafka no comparten una transaccion local. Un proceso puede caer despues
de publicar un mensaje y antes de registrar su entrega, generando duplicados.

## Decision

Confirmar ticket, historial y evento outbox en una transaccion PostgreSQL.
El publicador entrega el evento a Kafka y luego marca su entrega; cualquier
republicacion es esperable. Usar `ticketId` como clave de particion y retencion
local inicial de siete dias, sin depender de Kafka como historial funcional.

Cada consumidor deduplicara por consumidor y `eventId` mediante inbox y restriccion
unica. IA persistira inbox y trabajo antes de confirmar el offset, y resultado y
outbox en una transaccion antes de publicar su respuesta. Aunque el inventario de
tablas de la seccion 9 no enumera `ai.inbox`, su transaccion critica 2 la exige:
se incluira esa tabla en las migraciones de I-02.

Tickets comprobara `analysisId`, `contentRevision` y `attempt` al recibir resultados.
Un duplicado no generara otra propuesta ni otra entrega logica a n8n. Un resultado
obsoleto se auditara sin alterar el estado vigente. La deduplicacion por evento no
sustituye las restricciones de negocio por analisis/intento y por propuesta publicada.

Las entregas HTTP a n8n tambien usaran outbox y `deliveryId`, con deduplicacion por
`proposalId`. Solo tickets programara reintentos de analisis; IA utilizara trabajos
durables, lease y fencing token. Un evento ilegible se confirmara unicamente tras
su entrega durable a DLQ; su reejecucion sera administrativa y auditada.

## Consecuencias

Se garantiza entrega al menos una vez con efectos idempotentes; no se promete
exactamente una vez entre PostgreSQL y Kafka. Hay mas tablas, workers y reconciliacion.
Se descarta guardar y publicar sin outbox porque una caida intermedia perderia eventos.

## Verificacion

Pruebas pendientes de I-02/I-06: AT-10 (duplicados), AT-11 (revision obsoleta),
AT-16 (Kafka caido), AT-17 (caida entre publicacion y marca) y AT-20 (trabajo durable).
La revision documental de este ADR no demuestra entrega ni recuperacion real.
