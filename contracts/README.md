# Contratos de SupportHub

[openapi.yaml](openapi.yaml) define la API REST publica prevista con OpenAPI 3.0.3.
Se mantiene como contrato de diseno; todavia no hay controladores HTTP implementados.
Las descripciones documentan las reglas que deberan aplicar los casos de uso.

## Alcance

Once operaciones de la seccion 7 de la especificacion: crear, consultar, listar,
editar, historial, propuestas, reintento de IA, decision, cierre, reapertura y evidencia.
Incluye solicitudes, respuestas, referencias, publicaciones y Problem Details.
La base local es `http://localhost:8080/api/v1`; fuera de localhost se exige TLS.

## Convenciones concretadas para implementar

- Paginacion basada en cero: `page=0`, `size=20`, maximo 100. Historial y propuestas
  usan el mismo formato de pagina. El listado de tickets ordena ascendentemente
  por `createdAt, ticketId`; propuestas por `createdAt, proposalId`; historial por secuencia.
- `createdFrom` y `createdTo` son limites inclusivos UTC de creacion; intervalo
  invertido devuelve 400. Los filtros de estado y categoria se combinan.
- PATCH requiere `expectedVersion` y al menos titulo o descripcion. Si se envian
  ambos, ambos deben ser validos. Un campo ausente conserva su valor.
- Reintento manual requiere `expectedVersion`; cierre y reapertura exigen ademas
  motivo. Estas convenciones concretan el control de concurrencia en esas operaciones.
- Los cuerpos de entrada son cerrados (`additionalProperties: false`): no admiten
  `approvedBy`, `actor` ni otros campos libres fuera del esquema.
- La demo utiliza operadores y administradores con permiso de operador para la API.
  No se incorpora un rol de solicitante ni se afirma aislamiento por propietario.
  `AI_SERVICE` no tiene acceso a estas rutas publicas; su reconciliacion interna
  se definira en el incremento correspondiente.
- `x-roles` documenta roles permitidos; ADMIN no sustituye identidad humana con
  permiso OPERATOR para decidir. La ruta de decisiones permite workflow autenticado
  solo si aporta identidad humana verificable. El transporte concreto de esa
  prueba sigue pendiente segun ADR-0004, antes de habilitar I-05.
- Una respuesta de decision incluye `reply` al aprobar y lo omite al rechazar.
  Los resultados de un replay identico son los persistidos al decidir, aunque
  posteriormente haya cambiado el ticket.
- `category` y `priority` se omiten hasta clasificar; no son enums con valor nulo.
  El estado anterior de la creacion en el historial si admite nulo.
- Los hashes usan `sha256:` seguido de 64 caracteres hexadecimales minusculos.
  Canonicalizacion y calculo del hash de propuesta se resolveran antes de aceptar decisiones.
- El cuerpo tiene un limite de 32768 bytes (400 si se excede), independiente de los
  limites de caracteres. Normalizacion, HTML ejecutable, UTC, identidad, autorizacion,
  revision vigente, idempotencia y atomicidad necesitan comprobacion en ejecucion:
  declararlos en OpenAPI o con extensiones `x-` no los implementa.

Estas convenciones no modifican los requisitos funcionales de la especificacion.
Las respuestas de salida permanecen extensibles para evolucion compatible del contrato.

## Validacion

Desde la raiz, para compilar y ejecutar todas las pruebas:

```powershell
mvn -B verify
```

Para validar solamente OpenAPI:

```powershell
mvn -B -pl services/ticket-service -Dtest=OpenApiContractTest test
```

`OpenApiContractTest` carga el archivo real como recurso Maven, valida sintaxis y
referencias con Swagger Parser y comprueba reglas contractuales. Incluye un caso
negativo con una referencia rota. No sustituye pruebas HTTP, de autorizacion ni
de persistencia; tampoco ejecuta validacion de payloads de clientes contra el esquema.

Referencia de formato: [OpenAPI 3.0.3](https://spec.openapis.org/oas/v3.0.3.html).
