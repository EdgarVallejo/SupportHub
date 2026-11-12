# ADR-0004: Aprobacion humana ligada a propuesta y hash

- Estado: aceptado; mecanismo concreto de identidad pendiente antes de habilitar I-05.
- Fecha: 2026-11-11.
- Trazabilidad: RF-09, RF-10, RF-11; INV-01, INV-07; secciones 7, 9 y 12.

## Contexto

Una decision sobre un borrador no debe autorizar otro contenido, una revision
obsoleta ni una publicacion solicitada exclusivamente por la IA o por un servicio.

## Decision

Persistir la propuesta inmutable y su hash SHA-256 calculado en el servidor sobre
una representacion canonica versionada de su contenido publicable, accion y
referencias. Los detalles de canonicalizacion y vectores de prueba se fijaran
al implementar propuestas, antes de aceptar decisiones. No confiar en un hash
declarado por el modelo. Una edicion crea otra propuesta y otro hash calculado.

La decision identificara `decisionId`, `proposalId`, `proposalHash`, `expectedVersion`,
decision y motivo. El servidor comprobara rol, identidad humana verificable,
revision/intento vigente, hash, estado y vencimiento inicial de 24 horas.
El actor no provendra de un campo libre `approvedBy`. Una credencial de workflow
por si sola no acredita aprobacion humana: n8n debera autenticar al operador y
transmitir su identidad mediante un mecanismo verificable por la API.

Identica repeticion de `decisionId` devolvera el resultado persistido; cambiar su
contenido devolvera 409. Tambien devolveran 409 los conflictos de version, hash,
vigencia o revision. Un rechazo conservara motivo y propuesta, sin publicar.
El endpoint no se habilitara mientras la identidad humana no este resuelta y probada.

## Consecuencias

La IA y n8n no pueden aprobar mediante campos libres. Se necesita implementar
canonicalizacion, autenticacion y controles transaccionales; `canTransitionTo`
solo describe el grafo y no sustituye ninguna de estas comprobaciones.
Se descarta aprobar con un GET o con un enlace sin sesion autenticada.

## Verificacion

I-05 debera pasar AT-04, AT-05, AT-06, AT-07, AT-18, AT-19 y AT-22. Se incluiran
vectores deterministas del hash y casos de replay y propuesta obsoleta. Este ADR
documenta el contrato; no habilita aprobaciones ni afirma seguridad implementada.
