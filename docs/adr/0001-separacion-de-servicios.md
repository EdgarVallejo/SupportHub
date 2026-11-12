# ADR-0001: Separacion de servicios y propiedad de datos

- Estado: aceptado.
- Fecha: 2026-11-11.
- Trazabilidad: RF-05, RF-13; INV-06, INV-07; especificacion, secciones 6 y 9.

## Contexto

La API debe aceptar tickets aunque el proceso de IA este detenido. La generacion
de propuestas y las decisiones humanas tienen responsabilidades y permisos diferentes.

## Decision

Mantener dos aplicaciones Java 21 independientes: `ticket-service` y `ai-service`.
El POM padre centraliza versiones y compilacion, sin dependencias de un servicio al otro.
Los contratos REST y eventos versionados definen su comunicacion.

`ticket-service` es el unico escritor de tickets, propuestas recibidas, decisiones,
respuestas e historial, en el esquema `support`. Posee la API, la reconciliacion y
el planificador de reintentos de analisis. `ai-service` posee corpus, fragmentos,
embeddings, trabajos e inferencias en el esquema `ai`; no decide aprobaciones ni publica respuestas.

Una instancia PostgreSQL local alojara ambos esquemas, con usuarios y migraciones
separados. Los roles de cada servicio no tendran escritura en el esquema ajeno.
n8n utilizara la API autenticada; no accedera directamente a las tablas de negocio.

## Consecuencias

Se puede detener y recuperar IA sin impedir la entrada de tickets. Se requieren
contratos explicitos, correlacion y consistencia eventual entre ambos servicios.
Se descarta un unico proceso porque acoplaria disponibilidad y permisos de IA a la API.
La separacion de modulos por si sola no demuestra aislamiento en PostgreSQL.

## Verificacion

I-00-01 compila ambos modulos. I-01/I-02 deberan verificar permisos de base de datos
y transacciones; AT-08 comprobara recepcion y recuperacion con IA detenida, y RF-13
exigira trazabilidad por ticket y correlacion. Esas pruebas aun estan pendientes.
