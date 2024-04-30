package com.develhope.spring.Vehicles.Request;

import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleRequest {

    private String brand;
    private String model;
    private Integer displacement;
    private String color;
    private Integer power;
    private String transmission;
    private Integer registrationYear;
    private String powerSupply;
    private BigDecimal price;
    private BigDecimal discount;
    private String accessories;
    private Boolean isNew;
    private VehicleStatus vehicleStatus;
    private VehicleType vehicleType;
}
