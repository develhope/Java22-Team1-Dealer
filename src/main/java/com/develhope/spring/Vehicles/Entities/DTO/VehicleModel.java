package com.develhope.spring.Vehicles.Entities.DTO;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class VehicleModel {

    private Long vehicleId;
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
    private VehicleStatus vehicleStatus;
    private VehicleType vehicleType;

    public VehicleModel(String brand, String model, Integer displacement, String color, Integer power, String transmission,
                        Integer registrationYear, String powerSupply, BigDecimal price, BigDecimal discount, List <String> accessories,
                        Boolean isNew, VehicleStatus vehicleStatus, VehicleType vehicleType) {
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

    public static VehicleModel entityToModel(VehicleEntity vehicleEntity) {
        return new VehicleModel(vehicleEntity.getVehicleId(), vehicleEntity.getBrand(), vehicleEntity.getModel(),
                vehicleEntity.getDisplacement(), vehicleEntity.getColor(), vehicleEntity.getPower(), vehicleEntity.getTransmission(),
                vehicleEntity.getRegistrationYear(), vehicleEntity.getPowerSupply(), vehicleEntity.getPrice(), vehicleEntity.getDiscount(),
                vehicleEntity.getAccessories(), vehicleEntity.getIsNew(), vehicleEntity.getVehicleStatus(), vehicleEntity.getVehicleType());
    }

    public static VehicleModel DTOtoModel(VehicleDTO vehicleDTO) {
        return new VehicleModel(vehicleDTO.getVehicleId(), vehicleDTO.getBrand(), vehicleDTO.getModel(), vehicleDTO.getDisplacement(),
                vehicleDTO.getColor(), vehicleDTO.getPower(), vehicleDTO.getTransmission(), vehicleDTO.getRegistrationYear(),
                vehicleDTO.getPowerSupply(), vehicleDTO.getPrice(), vehicleDTO.getDiscount(), vehicleDTO.getAccessories(),
                vehicleDTO.getIsNew(), vehicleDTO.getVehicleStatus(), vehicleDTO.getVehicleType());
    }

    public static VehicleEntity modelToEntity(VehicleModel vehicleModel) {
        return new VehicleEntity(vehicleModel.getVehicleId(), vehicleModel.getBrand(), vehicleModel.getModel(), vehicleModel.getDisplacement(),
                vehicleModel.getColor(), vehicleModel.getPower(), vehicleModel.getTransmission(), vehicleModel.getRegistrationYear(),
                vehicleModel.getPowerSupply(), vehicleModel.getPrice(), vehicleModel.getDiscount(), vehicleModel.getAccessories(),
                vehicleModel.getIsNew(), vehicleModel.getVehicleStatus(), vehicleModel.getVehicleType());
    }

    public static VehicleDTO modelToDTO(VehicleModel vehicleModel) {
        return new VehicleDTO(vehicleModel.getVehicleId(), vehicleModel.getBrand(), vehicleModel.getModel(), vehicleModel.getDisplacement(),
                vehicleModel.getColor(), vehicleModel.getPower(), vehicleModel.getTransmission(), vehicleModel.getRegistrationYear(),
                vehicleModel.getPowerSupply(), vehicleModel.getPrice(), vehicleModel.getDiscount(), vehicleModel.getAccessories(),
                vehicleModel.getIsNew(), vehicleModel.getVehicleStatus(), vehicleModel.getVehicleType());
    }
}
