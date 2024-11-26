package com.magenta.persistence.entity;

/**
 * Tipo de notificación.
 */
public enum NotificationType {
    FIVE_DAYS_BEFORE, // Cinco días antes
    TWO_DAYS_BEFORE, // Dos días antes
    ONE_DAY_BEFORE, // Un día antes
    ONE_HOUR_BEFORE, // Una hora antes
    APPOINTMENT_CREATED,
    APPOINTMENT_CANCELLED
}