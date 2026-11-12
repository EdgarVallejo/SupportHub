# Matriz de versiones

Validacion parcial hasta I-00-03 el 11 de noviembre de 2026, Windows 11 x64.
Esta matriz se ampliara al incorporar Spring Boot e infraestructura; no demuestra
todavia compatibilidad de todo el stack.

| Componente | Version | Evidencia |
|---|---|---|
| Eclipse Temurin JDK | 21.0.13+9 | `java -version`; compilacion release 21 |
| Apache Maven | 3.9.16 | `mvn -version` y reactor de dos modulos |
| JUnit Jupiter | 5.13.4 | 30 ejecuciones de pruebas sin fallos |
| Maven Compiler Plugin | 3.14.1 | Compilacion de produccion y pruebas |
| Maven Surefire Plugin | 3.5.4 | Ejecucion de JUnit Platform |
| Maven Resources Plugin | 3.4.0 | Fases de recursos del reactor |
| Maven JAR Plugin | 3.5.0 | Empaquetado de ambos modulos |
| Swagger Parser v3 (solo pruebas) | 2.1.27 | OpenAPI valido y referencia rota detectada; 25 casos contractuales |

Referencias oficiales de las versiones elegidas:

- [JUnit 5.13.4](https://docs.junit.org/5.13.4/release-notes/index.html).
- [Compiler 3.14.1](https://maven.apache.org/components/plugins-archives/maven-compiler-plugin-3.14.1/usage.html).
- [Surefire 3.5.4](https://maven.apache.org/components/surefire-archives/surefire-3.5.4/download.cgi).

Los plugins de recursos y JAR se resolvieron desde Maven Central en la primera
ejecucion y se fijaron explicitamente en el POM. No se usan rangos ni `LATEST`.
