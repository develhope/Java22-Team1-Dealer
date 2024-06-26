package com.develhope.spring.vehicles.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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
    private List<String> accessories;
    private Boolean isNew;
    private String vehicleStatus;
    private String vehicleType;
}
