package com.magenta.persistence.entity;

/**
 * Tipo de evento.
 */
public enum EventType {
    WEDDING("Boda"), // Boda
    SWEET_FIFTEEN("Quinceañera"), // Quinceañera
    COMMUNION("Comunión"), // Comunión
    BAPTISM("Bautizo"), // Bautizo
    CORPORATE("Evento Corporativo"), // Evento Corporativo
    OTHER("Otro"); // Otro

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}