package com.develhope.spring.rent.model;

import com.develhope.spring.rent.DTO.RentDTO;
import com.develhope.spring.rent.entities.RentEntity;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RentModel {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal dailyCost;
    private boolean isPaid;
    private VehicleEntity vehicleEntity;
    private BigDecimal totalCost;
    private Long id;



    public RentModel(LocalDate startDate, LocalDate endDate, BigDecimal dailyCost, boolean isPaid, VehicleEntity vehicleEntity, BigDecimal totalCost, Long id) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyCost = dailyCost;
        this.isPaid = isPaid;
        this.vehicleEntity = vehicleEntity;
        this.totalCost = totalCost;
        this.id = id;
    }

    public RentModel(LocalDate startDate, LocalDate endDate, BigDecimal dailyCost, boolean isPaid, VehicleEntity vehicleEntity, BigDecimal totalCost) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyCost = dailyCost;
        this.isPaid = isPaid;
        this.vehicleEntity = vehicleEntity;
        this.totalCost = totalCost;
    }

    public static RentEntity modelToEntity(RentModel rentModel) {
        return new RentEntity(rentModel.getStartDate(), rentModel.getEndDate(), rentModel.getDailyCost(), rentModel.isPaid(), rentModel.getVehicleEntity(), rentModel.getTotalCost(), rentModel.getId());
    }
    public static RentDTO modelToDTO(RentModel rentModel) {
        return new RentDTO(rentModel.getStartDate(), rentModel.getEndDate(), rentModel.getDailyCost(), rentModel.isPaid(), rentModel.getVehicleEntity(), rentModel.getTotalCost(), rentModel.getId());
    }

    public static RentModel dtoToModel(RentDTO rentDTO) {
        return new RentModel(rentDTO.getStartDate(), rentDTO.getEndDate(), rentDTO.getDailyCost(), rentDTO.getIsPaid(), rentDTO.getVehicleEntity(), rentDTO.getTotalCost(), rentDTO.getId());
    }

    public static RentModel entityToModel(RentEntity rentEntity) {
        return new RentModel(rentEntity.getStartDate(), rentEntity.getEndDate(), rentEntity.getDailyCost(), rentEntity.getIsPaid(), rentEntity.getVehicleId(), rentEntity.getTotalCost(), rentEntity.getId());
    }
}

