# SupportHub

MVP de soporte tecnico asistido por IA para la empresa ficticia Nebula Shop.
El sistema propondra respuestas con referencias y exigira aprobacion humana
antes de publicarlas en el historial interno del ticket.

## Estado

Estructura inicial del proyecto. I-00 (contratos y entorno base) esta pendiente
de implementacion; los servicios y la infraestructura aun no estan configurados.

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
Los comandos de compilacion y arranque se agregaran durante I-00.

El script `scripts/setup/install-wsl.ps1` es una utilidad opcional para Windows;
requiere permisos de administrador y puede requerir reiniciar el equipo.
