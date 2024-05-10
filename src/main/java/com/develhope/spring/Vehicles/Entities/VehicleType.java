package com.develhope.spring.Vehicles.Entities;

public enum VehicleType {

    CAR,
    MOTORBIKE,
    SCOOTER,
    VAN,
    NOT_DEFINED;


    public static VehicleType convertFromString(String type) {
        return switch (type.toLowerCase()) {
            case "car" -> VehicleType.CAR;
            case "motorbike" -> VehicleType.MOTORBIKE;
            case "scooter" -> VehicleType.SCOOTER;
            case "van" -> VehicleType.VAN;
            default -> VehicleType.NOT_DEFINED;
        };
    }
}
