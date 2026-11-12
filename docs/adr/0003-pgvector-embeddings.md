# ADR-0003: pgvector y procedencia de embeddings

- Estado: aceptado.
- Fecha: 2026-11-11.
- Trazabilidad: RF-06, RF-08; INV-05; secciones 6, 10 y 11.

## Contexto

Una respuesta debe citar evidencia recuperada de una version identificable del
corpus ficticio. Mezclar modelos o dimensiones vuelve incoherente la recuperacion.

## Decision

Usar pgvector dentro del esquema `ai` de PostgreSQL. La extension se aprovisionara
con permisos administrativos en infraestructura; el rol de IA no necesitara ser superusuario.
Documentos y versiones seran inmutables; los fragmentos conservaran documento,
version, seccion, ID de fragmento y hash SHA-256 del contenido.

Persistir proveedor/modelo, version y dimension de embeddings junto con la version
del corpus. Un cambio de modelo o dimension exige un indice compatible y reingesta
explicita; no se compararan vectores de espacios diferentes. Consulta e indice
deben usar la misma configuracion, validada antes de recuperar.

Recuperar top-4 con un maximo de dos fragmentos por documento. Fragmentar por
seccion, hasta 500 tokens y solapamiento de hasta 80 si es necesario partirla.
La metrica y el umbral se calibraran con los casos de evaluacion antes del modo real.
No se elige una dimension ni un umbral universal antes de seleccionar el adaptador.

## Consecuencias

Corpus, procedencia y vectores comparten almacenamiento local. El modelo de
embeddings condiciona indice y reingesta. Se descarta agregar una base vectorial
independiente al MVP para evitar otra dependencia operativa.
Validar que una cita existe no demuestra que todas las afirmaciones sean fieles.

## Verificacion

I-03 validara manifiesto, diez documentos, metadatos e incompatibilidad de embeddings.
I-04 ejecutara AT-12 y AT-13, la evaluacion versionada y revision semantica humana.
Una prueba stub no acreditara embeddings reales. Extension y proveedor aun pendientes.
