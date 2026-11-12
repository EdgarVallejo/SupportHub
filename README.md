# SupportHub

MVP de soporte tecnico asistido por IA para la empresa ficticia Nebula Shop.
El sistema propondra respuestas con referencias y exigira aprobacion humana
antes de publicarlas en el historial interno del ticket.

## Estado

I-00 esta en progreso. La tarea I-00-01 incorpora la base Maven de los dos servicios
y el contrato de transiciones de estados, con pruebas unitarias JUnit.
Los servicios aun no tienen API ni procesamiento IA; falta configurar la infraestructura.
I-00-02 documenta las seis [decisiones de arquitectura](docs/adr/README.md),
incluidos los controles por implementar y las decisiones pendientes.
I-00-03 agrega el [contrato REST OpenAPI](contracts/README.md) con las once
operaciones previstas y 25 pruebas de contrato, ademas de las 30 de estados.

La especificacion del proyecto esta en [docs/specs/supporthub.md](docs/specs/supporthub.md).

## Estructura

```text
contracts/           Contratos REST y eventos
docs/adr/            Decisiones de arquitectura
docs/specs/          Especificacion del proyecto
evaluation/          Casos de evaluacion RAG
infra/               Infraestructura local
knowledge/kb-v1/     Corpus ficticio versionado
scripts/demo/        Automatizacion de la demostracion
scripts/setup/       Preparacion del entorno
services/ai-service/ Servicio de analisis y generacion
services/ticket-service/ API y gestion de tickets
workflows/n8n/       Flujo de aprobacion humana
```

## Entorno previsto

Java 21, Maven y Docker Desktop con el motor Linux mediante WSL 2 en Windows.
El modo inicial de IA sera simulado (`stub`), sin credenciales de pago.

Desde la raiz del repositorio, compila y ejecuta las pruebas con:

```powershell
mvn -B verify
```

Para ejecutar solo las pruebas de tickets y construir sus dependencias locales:

```powershell
mvn -B -pl services/ticket-service -am test
```

La primera ejecucion descarga dependencias de Maven Central. Los informes JUnit
quedan en `services/ticket-service/target/surefire-reports/`, excluidos de Git.
`ai-service` es por ahora un modulo reservado y no contiene pruebas ni codigo ejecutable.
El backlog se encuentra en [docs/backlog.md](docs/backlog.md).

El script `scripts/setup/install-wsl.ps1` es una utilidad opcional para Windows;
requiere permisos de administrador y puede requerir reiniciar el equipo.
