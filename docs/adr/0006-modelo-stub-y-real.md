# ADR-0006: Adaptadores de IA y modo inicial stub

- Estado: aceptado para stub; proveedor y modelos reales pendientes de seleccion explicita.
- Fecha: 2026-11-11.
- Trazabilidad: RF-06, RF-07, RF-08, RF-12; INV-07; secciones 6, 11, 13 y 16.

## Contexto

CI y demo de recuperacion deben ser reproducibles sin credenciales pagadas.
Una simulacion no acredita integracion con un proveedor ni calidad de un modelo real.

## Decision

Definir puertos Java `EmbeddingPort`, `KnowledgeRetriever`, `SupportModelPort` y
`ProposalValidator`, con adaptadores `stub` y `real` separados del dominio.
`ActionPublisher` pertenecera a tickets y no se expondra como herramienta de IA.

El modo inicial sera `stub`: datos ficticios, resultados deterministas y ninguna
llamada pagada. El adaptador simulado de generacion citara evidencia entregada y
pasara por el mismo validador de salida. Los vectores simulados identificaran su
propia version y dimension, separados del indice real. Su diseno concreto y los
casos esperados se implementaran y probaran en I-02/I-03.

El modo `real` se activara expresamente por configuracion y fallara si esta incompleta;
no cambiara silenciosamente a stub. Proveedor, modelo de generacion y embeddings,
versiones, dimension, limites y costes aceptados quedaran registrados antes de activarlo.
No se incorpora por ahora un SDK ni proveedor de pago.

Ambos modos validaran enums, esquema, accion y referencias. Sin evidencia suficiente:
`NONE`, investigacion humana y revision manual. El modelo no ejecutara acciones ni
decidira quien aprobo. Tickets gestionara el presupuesto de reintentos, sin reintentos
ocultos del cliente del proveedor. Toda inferencia registrara modo, modelo, prompt y corpus.

## Consecuencias

Se puede avanzar sin contratar servicios; habra que mantener y evaluar dos adaptadores.
La seleccion real sigue abierta y bloquea la aceptacion de I-04, no el desarrollo stub.
Se descarta tratar resultados simulados como prueba de integracion real.

## Verificacion

CI ejecutara unidad e integracion en stub sin secretos. I-04 debera pasar AT-03,
AT-12, AT-13 y AT-14 y documentar al menos una inferencia y una consulta de embeddings
reales. AT-09 verificara los fallos del proveedor y el presupuesto de reintentos.
