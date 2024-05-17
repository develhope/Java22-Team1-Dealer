package com.develhope.spring.Vehicles.Entities;

public enum VehicleStatus {

    RENTABLE,
    PURCHASABLE,
    RENTED,
    SOLD,
    ORDERED,
    NOT_AVAILABLE;

    public static VehicleStatus convertFromString(String status) {
        return switch (status.toLowerCase()) {
            case "rentable" -> VehicleStatus.RENTABLE;
            case "purchasable" -> VehicleStatus.PURCHASABLE;
            case "rented" -> VehicleStatus.RENTED;
            case "sold" -> VehicleStatus.SOLD;
            case "ordered" -> VehicleStatus.ORDERED;
            default -> VehicleStatus.NOT_AVAILABLE;
        };
    }
}
