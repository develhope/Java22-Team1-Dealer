package com.develhope.spring.Vehicles.DTO;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Entities.VehicleType;

import java.math.BigDecimal;

public class VehicleModel {

    private Long id;
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

    public VehicleModel(String brand, String model, Integer displacement, String color, Integer power, String transmission, Integer registrationYear, String powerSupply, BigDecimal price, BigDecimal discount, String accessories, Boolean isNew, VehicleStatus vehicleStatus, VehicleType vehicleType) {
        this.brand = brand;
        this.model = model;
        this.displacement = displacement;
        this.color = color;
        this.power = power;
        this.transmission = transmission;
        this.registrationYear = registrationYear;
        this.powerSupply = powerSupply;
        this.price = price;
        this.discount = discount;
        this.accessories = accessories;
        this.isNew = isNew;
        this.vehicleStatus = vehicleStatus;
        this.vehicleType = vehicleType;
    }

    public VehicleEntity toEntity() {
        return new VehicleEntity(this.id, this.brand, this.model, this.displacement, this.color, this.power, this.transmission, this.registrationYear,
                this.powerSupply, this.price, this.discount, this.accessories, this.isNew, this.vehicleStatus, this.vehicleType);
    }

    public VehicleDTO toDTO() {
        return new VehicleDTO(this.brand, this.model, this.displacement, this.color, this.power, this.transmission, this.registrationYear,
                this.powerSupply, this.price, this.discount, this.accessories, this.isNew, this.vehicleStatus, this.vehicleType);
    }
}
