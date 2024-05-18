package com.develhope.spring.order.Entities.enums;
//ordinato
//cancellato
//spedito
//consegnato
public enum OrderStatus {
    ORDERED,
    CANCELED,
    SHIPPED,
    DELIVERED;

    public static OrderStatus convertFromString(String status) {
        return switch(status.toLowerCase()) {
            case "canceled" -> OrderStatus.CANCELED;
            case "shipped" -> OrderStatus.SHIPPED;
            case "delivered" -> OrderStatus.DELIVERED;
            default -> OrderStatus.ORDERED;
        };
    }
}
