package com.develhope.spring.vehicles.entities;

public enum VehicleStatus {

    RENTABLE,
    PURCHASABLE,
    RENTED,
    SOLD,
    ORDERED,
    ORDERABLE,
    NOT_SET;

    public static VehicleStatus convertFromString(String status) {
        return switch (status.toLowerCase()) {
            case "rentable" -> VehicleStatus.RENTABLE;
            case "purchasable" -> VehicleStatus.PURCHASABLE;
            case "rented" -> VehicleStatus.RENTED;
            case "sold" -> VehicleStatus.SOLD;
            case "ordered" -> VehicleStatus.ORDERED;
            case "orderable" -> VehicleStatus.ORDERABLE;
            default -> VehicleStatus.NOT_SET;
        };
    }
}
