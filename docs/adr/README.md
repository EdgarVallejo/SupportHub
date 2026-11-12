# Decisiones de arquitectura

Un ADR registra contexto, decision, consecuencias y forma de comprobarla.
Estado **aceptado** significa decision de diseno; no implica funcionalidad implementada.
Un cambio futuro se documentara en otro ADR que indique cual sustituye.

| ADR | Decision | Requisitos e invariantes |
|---|---|---|
| [0001](0001-separacion-de-servicios.md) | Dos servicios con propiedad separada | RF-05, RF-13, INV-06, INV-07 |
| [0002](0002-outbox-inbox.md) | Outbox/inbox y entrega al menos una vez | RF-01, RF-05, RF-12, RF-15, INV-02, INV-03, INV-04 |
| [0003](0003-pgvector-embeddings.md) | pgvector y embeddings versionados | RF-06, RF-08, INV-05 |
| [0004](0004-aprobacion-ligada-a-hash.md) | Aprobacion humana ligada a propuesta y hash | RF-09, RF-10, RF-11, INV-01, INV-07 |
| [0005](0005-publicacion-interna.md) | Publicacion interna como unico efecto | RF-10, RF-11, INV-01, INV-02 |
| [0006](0006-modelo-stub-y-real.md) | Adaptadores de IA; modo inicial stub | RF-06, RF-07, RF-08, RF-12, INV-07 |

Fuentes: [especificacion](../specs/supporthub.md), secciones 5, 6, 8, 9, 11, 12,
13 y 17. La [evidencia de I-00-02](../evidence/I-00-02.md) distingue las
validaciones documentales de las futuras pruebas de comportamiento.
