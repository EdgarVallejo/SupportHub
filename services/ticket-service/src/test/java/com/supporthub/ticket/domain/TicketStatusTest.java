package com.supporthub.ticket.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Prueba unitaria del contrato de estados definido en la seccion 5; no usa infraestructura. */
class TicketStatusTest {

    // La tabla esperada es explicita e independiente del algoritmo productivo.
    // Cubre las 25 combinaciones: 12 aristas permitidas y 13 saltos rechazados.
    @ParameterizedTest(name = "{0} -> {1}: permitido={2}")
    @CsvSource({
            "PENDING_AI, PENDING_AI, false",
            "PENDING_AI, AWAITING_APPROVAL, true",
            "PENDING_AI, MANUAL_REVIEW, true",
            "PENDING_AI, RESOLVED, false",
            "PENDING_AI, CLOSED, true",
            "AWAITING_APPROVAL, PENDING_AI, true",
            "AWAITING_APPROVAL, AWAITING_APPROVAL, false",
            "AWAITING_APPROVAL, MANUAL_REVIEW, true",
            "AWAITING_APPROVAL, RESOLVED, true",
            "AWAITING_APPROVAL, CLOSED, true",
            "MANUAL_REVIEW, PENDING_AI, true",
            "MANUAL_REVIEW, AWAITING_APPROVAL, false",
            "MANUAL_REVIEW, MANUAL_REVIEW, false",
            "MANUAL_REVIEW, RESOLVED, false",
            "MANUAL_REVIEW, CLOSED, true",
            "RESOLVED, PENDING_AI, true",
            "RESOLVED, AWAITING_APPROVAL, false",
            "RESOLVED, MANUAL_REVIEW, false",
            "RESOLVED, RESOLVED, false",
            "RESOLVED, CLOSED, true",
            "CLOSED, PENDING_AI, true",
            "CLOSED, AWAITING_APPROVAL, false",
            "CLOSED, MANUAL_REVIEW, false",
            "CLOSED, RESOLVED, false",
            "CLOSED, CLOSED, false"
    })
    void matchesSpecifiedTransitionGraph(TicketStatus current, TicketStatus target, boolean expected) {
        // Dado un par de estados, consulta la regla y contrasta el resultado normativo.
        assertEquals(expected, current.canTransitionTo(target));
    }

    @ParameterizedTest(name = "{0} rechaza un destino ausente")
    @EnumSource(TicketStatus.class)
    void rejectsMissingTarget(TicketStatus current) {
        // Ninguno de los cinco estados puede aceptar null como destino.
        assertThrows(NullPointerException.class, () -> current.canTransitionTo(null));
    }
}
