package com.magenta.persistence.entity;

public enum NotificationType {
    FIVE_DAYS_BEFORE,
    TWO_DAYS_BEFORE,
    ONE_DAY_BEFORE,
    ONE_HOUR_BEFORE,
    APPOINTMENT_CREATED,  // Nueva notificación para citas creadas
    APPOINTMENT_CANCELLED // También podríamos agregar esta para cancelaciones
}