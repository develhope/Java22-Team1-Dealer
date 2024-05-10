package com.develhope.spring.Vehicles.Entities;

public enum VehicleStatus {

    RENTABLE,
    PURCHASABLE,
    NOT_AVAILABLE;

    public static VehicleStatus convertFromString(String status) {
        return switch (status.toLowerCase()) {
            case "rentable" -> VehicleStatus.RENTABLE;
            case "purchasable" -> VehicleStatus.PURCHASABLE;
            default -> VehicleStatus.NOT_AVAILABLE;
        };
    }
}
