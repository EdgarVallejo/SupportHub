package com.supporthub.ticket.contract;

import com.supporthub.ticket.domain.TicketStatus;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Verifica el contrato versionado; estas pruebas no invocan una API implementada. */
class OpenApiContractTest {
    private static OpenAPI api;
    private static String contract;

    @BeforeAll
    static void loadAndValidateContract() throws IOException {
        // Maven copia el archivo real como recurso; no se mantiene una copia de prueba manual.
        try (var source = OpenApiContractTest.class.getResourceAsStream("/contracts/openapi.yaml")) {
            assertNotNull(source, "Maven debe incluir el contrato en los recursos de prueba");
            contract = new String(source.readAllBytes(), StandardCharsets.UTF_8);
        }
        // Resolver referencias permite detectar esquemas ausentes, ademas de errores de sintaxis.
        var options = new ParseOptions();
        options.setResolve(true);
        SwaggerParseResult parsed = new OpenAPIV3Parser().readContents(contract, null, options);
        assertNotNull(parsed.getOpenAPI(), "El documento debe ser OpenAPI valido");
        assertTrue(parsed.getMessages().isEmpty(), () -> "Errores OpenAPI: " + parsed.getMessages());
        api = parsed.getOpenAPI();
    }

    // Las once operaciones provienen de la seccion 7, no del listado del propio archivo.
    @ParameterizedTest(name = "{1} {0} devuelve {2}")
    @CsvSource({
            "/tickets, POST, 201",
            "/tickets, GET, 200",
            "/tickets/{id}, GET, 200",
            "/tickets/{id}, PATCH, 200",
            "/tickets/{id}/history, GET, 200",
            "/tickets/{id}/proposals, GET, 200",
            "/tickets/{id}/ai-retries, POST, 202",
            "/tickets/{id}/decisions, POST, 200",
            "/tickets/{id}/close, POST, 200",
            "/tickets/{id}/reopen, POST, 202",
            "/knowledge/documents/{id}/versions/{version}, GET, 200"
    })
    void exposesSpecifiedOperation(String path, String method, String success) {
        Operation operation = operation(path, method);
        assertTrue(operation.getResponses().containsKey(success));
        // Cada ruta mantiene los errores comunes en formato Problem Details compartido.
        for (String error : List.of("400", "401", "403", "429", "503")) {
            assertNotNull(operation.getResponses().get(error));
        }
        assertNotNull(operation.getExtensions().get("x-roles"));
        // Una operacion no puede anular la autenticacion global mediante security: [].
        assertNull(operation.getSecurity());
    }

    @Test
    void requiresAuthenticationAndPreservesApiPrefix() {
        assertEquals("http://localhost:8080/api/v1", api.getServers().getFirst().getUrl());
        assertEquals(Set.of("bearerAuth"), api.getSecurity().getFirst().keySet());
        assertEquals("bearer", api.getComponents().getSecuritySchemes().get("bearerAuth").getScheme());
        assertEquals(32768, ((Number) api.getExtensions().get("x-max-body-bytes")).intValue());
        // No deben aparecer rutas accidentales ni identificadores de operacion duplicados.
        var operations = api.getPaths().values().stream().flatMap(p -> p.readOperations().stream()).toList();
        assertEquals(11, operations.size());
        assertEquals(11, operations.stream().map(Operation::getOperationId).distinct().count());
    }

    // Comprueba limites de longitud que deberan reflejarse en Bean Validation al implementar la API.
    @ParameterizedTest(name = "{0}.{1}: longitud {2} a {3}")
    @CsvSource({
            "CreateTicketRequest, title, 5, 160",
            "CreateTicketRequest, description, 10, 8000",
            "UpdateTicketRequest, title, 5, 160",
            "UpdateTicketRequest, description, 10, 8000",
            "DecisionRequest, reason, 3, 1000",
            "ReasonVersionRequest, reason, 3, 1000"
    })
    void preservesInputLengths(String schemaName, String property, int min, int max) {
        Schema<?> field = (Schema<?>) schema(schemaName).getProperties().get(property);
        assertEquals(min, field.getMinLength());
        assertEquals(max, field.getMaxLength());
    }

    @Test
    void patchRequiresVersionAndAtLeastOneContentField() {
        Schema<?> patch = schema("UpdateTicketRequest");
        assertEquals(List.of("expectedVersion"), patch.getRequired());
        // anyOf permite uno o ambos campos; rechaza un PATCH que solo contiene la version.
        assertEquals(List.of(List.of("title"), List.of("description")),
                patch.getAnyOf().stream().map(Schema::getRequired).toList());
        assertEquals(1, ((Schema<?>) patch.getProperties().get("expectedVersion")).getMinimum().intValue());
    }

    @Test
    void decisionRequiresExactProposalAndRejectsFreeActorFields() {
        Schema<?> decision = schema("DecisionRequest");
        assertEquals(Set.of("decisionId", "proposalId", "proposalHash", "expectedVersion", "decision", "reason"),
                new HashSet<>(decision.getRequired()));
        assertEquals(Boolean.FALSE, decision.getAdditionalProperties());
        assertFalse(decision.getProperties().containsKey("approvedBy"));
        assertFalse(decision.getProperties().containsKey("actor"));
        assertEquals(List.of("APPROVE", "REJECT"), ((Schema<?>) decision.getProperties().get("decision")).getEnum());
        assertEquals("^sha256:[a-f0-9]{64}$", schema("ProposalHash").getPattern());
        Operation decide = operation("/tickets/{id}/decisions", "POST");
        assertEquals(Boolean.TRUE, decide.getExtensions().get("x-human-identity-required"));
        assertNotNull(decide.getResponses().get("409"));
    }

    @Test
    void stateContractAgreesWithDomainAndRestrictsProposedActions() {
        // Evita divergencia entre el grafo Java ya probado y los estados publicos del contrato.
        assertEquals(Arrays.stream(TicketStatus.values()).map(Enum::name).toList(), schema("TicketStatus").getEnum());
        assertEquals(List.of("PUBLISH_REPLY", "NONE"), schema("Action").getEnum());
        assertEquals(List.of("ACCESS", "ORDERS", "APPLICATION", "OTHER"), schema("Category").getEnum());
        assertEquals(List.of("LOW", "NORMAL", "HIGH"), schema("Priority").getEnum());
        assertEquals(List.of("QUEUED", "RUNNING", "RETRY_WAIT", "SUCCEEDED", "EXHAUSTED"), schema("AiStatus").getEnum());
    }

    @Test
    void paginationHasBoundedSizeAndNonNegativePage() {
        var parameters = api.getComponents().getParameters();
        assertEquals(100, parameters.get("Size").getSchema().getMaximum().intValue());
        assertEquals(1, parameters.get("Size").getSchema().getMinimum().intValue());
        assertEquals(0, parameters.get("Page").getSchema().getMinimum().intValue());
    }

    @Test
    void creationDocumentsLocationAndIdempotency() {
        Operation create = operation("/tickets", "POST");
        assertNotNull(create.getResponses().get("201").getHeaders().get("Location"));
        assertTrue(create.getParameters().stream().anyMatch(p -> "Idempotency-Key".equals(p.getName())));
        assertNotNull(create.getResponses().get("409"));
        assertEquals(Boolean.FALSE, schema("CreateTicketRequest").getAdditionalProperties());
    }

    @Test
    void errorsUseProblemDetailsWithoutRequiringAnExistingTicket() {
        for (String status : List.of("400", "401", "403", "404", "409", "429", "503")) {
            var content = api.getComponents().getResponses().get("Error" + status).getContent();
            assertNotNull(content.get("application/problem+json"));
        }
        assertTrue(schema("Problem").getRequired().containsAll(List.of("code", "correlationId")));
        assertFalse(schema("Problem").getRequired().contains("ticketId"));
    }

    @Test
    void validatorDetectsAnUnresolvedSchemaReference() {
        // Un contrato deliberadamente roto debe producir diagnosticos del parser.
        String invalid = contract.replace("#/components/schemas/DecisionRequest", "#/components/schemas/MissingDecision");
        var options = new ParseOptions();
        options.setResolve(true);
        var parsed = new OpenAPIV3Parser().readContents(invalid, null, options);
        assertFalse(parsed.getMessages().isEmpty(), "El validador debe detectar una referencia inexistente");
    }

    private static Schema<?> schema(String name) {
        // Centraliza la lectura para que una ausencia falle con el nombre del esquema.
        Schema<?> result = api.getComponents().getSchemas().get(name);
        assertNotNull(result, "Falta esquema: " + name);
        return result;
    }

    private static Operation operation(String path, String method) {
        // Accede al metodo HTTP exacto, sin depender del orden de las rutas en YAML.
        PathItem item = api.getPaths().get(path);
        assertNotNull(item, "Falta ruta: " + path);
        Operation result = item.readOperationsMap().get(PathItem.HttpMethod.valueOf(method));
        assertNotNull(result, "Falta operacion: " + method + " " + path);
        return result;
    }
}
