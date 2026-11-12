# ADR-0005: Publicacion interna como unico efecto del MVP

- Estado: aceptado.
- Fecha: 2026-11-11.
- Trazabilidad: RF-10, RF-11; INV-01, INV-02; secciones 2, 9 y 12.

## Contexto

La demo necesita mostrar un efecto verificable tras la aprobacion humana sin
introducir envios, pagos ni cambios en sistemas externos.

## Decision

Permitir unicamente `PUBLISH_REPLY` y `NONE` como acciones propuestas. La primera
inserta una respuesta en el historial interno del ticket, desde `ticket-service`.
`NONE` no autoriza publicacion. La IA y n8n no ejecutaran el efecto directamente.

En una sola transaccion: comprobar version y aprobacion vigente, insertar decision,
respuesta e historial y pasar a `RESOLVED`. La respuesta referenciara el `approvalId`
aprobado y la propuesta cuyo hash se verifico. Una restriccion unica por `proposalId`
impedira publicaciones duplicadas, incluso ante callbacks concurrentes.

Quedan excluidos correo, WhatsApp, reembolsos, cambios de pedidos y restablecimientos
efectivos de contrasenas. Un efecto externo futuro necesitara otro ADR, outbox,
idempotencia del proveedor y reconciliacion; no heredara atomicidad de la aprobacion.

## Consecuencias

El efecto se puede confirmar atomicamente con la decision en PostgreSQL.
La demo demuestra publicacion interna, sin afirmar que se contacto al solicitante.
Se descarta ejecutar herramientas autonomas o escribir en servicios externos.

## Verificacion

I-05 verificara AT-04 a AT-07, AT-18 y AT-19, con cero respuestas antes de aprobar
y una unica respuesta tras callbacks repetidos. Esas pruebas transaccionales siguen pendientes.
