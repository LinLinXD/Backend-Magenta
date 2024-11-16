package com.magenta.persistence.entity;

public enum EventType {
    WEDDING("Boda"),
    SWEET_FIFTEEN("Quinceañera"),
    COMMUNION("Comunión"),
    BAPTISM("Bautizo"),
    CORPORATE("Evento Corporativo"),
    OTHER("Otro");

    private final String displayName;

    EventType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}