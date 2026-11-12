package com.supporthub.ticket.domain;

import java.util.Objects;

/**
 * Estados funcionales de la seccion 5 de la especificacion de SupportHub.
 * El estado operativo de IA es independiente de este contrato.
 * Esta enumeracion describe el grafo permitido; no cambia tickets ni autoriza acciones.
 */
public enum TicketStatus {
    /** Espera un resultado de IA, incluidos sus reintentos. */
    PENDING_AI,
    /** Existe una propuesta vigente que espera una decision humana. */
    AWAITING_APPROVAL,
    /** Un operador debe investigar por rechazo, falta de evidencia o fallo agotado. */
    MANUAL_REVIEW,
    /** Una respuesta aprobada ya fue publicada en el historial interno. */
    RESOLVED,
    /** Un operador cerro el ticket; volver a procesarlo exige reapertura explicita. */
    CLOSED;

    /**
     * Consulta si el grafo admite pasar del estado actual al destino indicado.
     * Las guardas de identidad, aprobacion, hash, revision y concurrencia deberan
     * comprobarse en el agregado/caso de uso antes de aplicar cualquier cambio.
     * Por ejemplo, admitir AWAITING_APPROVAL a RESOLVED no demuestra aprobacion.
     *
     * @param target estado al que se pretende pasar
     * @return true si existe esa arista en la tabla normativa
     * @throws NullPointerException si no se proporciona un estado destino
     */
    public boolean canTransitionTo(TicketStatus target) {
        // Un destino ausente es un error de programacion, no una transicion valida.
        Objects.requireNonNull(target, "El estado destino es obligatorio");

        // Cada rama reproduce las salidas de un estado sin permitir saltos implicitos.
        // Permanecer en el mismo estado no es una transicion del grafo; la edicion
        // de contenido y los reintentos conservaran sus propias reglas de revision.
        return switch (this) {
            case PENDING_AI -> target == AWAITING_APPROVAL
                    || target == MANUAL_REVIEW || target == CLOSED;
            case AWAITING_APPROVAL -> target == RESOLVED || target == MANUAL_REVIEW
                    || target == PENDING_AI || target == CLOSED;
            case MANUAL_REVIEW, RESOLVED -> target == PENDING_AI || target == CLOSED;
            case CLOSED -> target == PENDING_AI;
        };
    }
}
