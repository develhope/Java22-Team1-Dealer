package com.develhope.spring.purchase.model;

import com.develhope.spring.purchase.DTO.PurchaseDTO;
import com.develhope.spring.purchase.entities.PurchaseEntity;
import com.develhope.spring.vehicles.model.VehicleModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PurchaseModel {
    private Long id;

    private Boolean isPaid;

    private VehicleModel vehicle;

    private LocalDate purchaseDate;


    public PurchaseModel( Boolean isPaid, VehicleModel vehicle, LocalDate purchaseDate) {
        this.isPaid = isPaid;
        this.vehicle = vehicle;
        this.purchaseDate = purchaseDate;
    }

    public static PurchaseEntity modelToEntity(PurchaseModel purchaseModel) {
        return new PurchaseEntity(purchaseModel.getId(),
                purchaseModel.getIsPaid(),
                VehicleModel.modelToEntity(purchaseModel.getVehicle()),
                purchaseModel.getPurchaseDate());
    }
    public static PurchaseDTO modelToDto(PurchaseModel purchaseModel) {
        return new PurchaseDTO(purchaseModel.getId(),
                purchaseModel.getIsPaid(),
                VehicleModel.modelToDTO(purchaseModel.getVehicle()),
                purchaseModel.getPurchaseDate());
    }

    public static PurchaseModel entityToModel(PurchaseEntity purchaseEntity) {
        return new PurchaseModel(purchaseEntity.getPurchaseId(),
                purchaseEntity.getIsPaid(),
                VehicleModel.entityToModel(purchaseEntity.getVehicle()),
                purchaseEntity.getPurchaseDate());
    }

    public static PurchaseModel dtoToModel(PurchaseDTO purchaseDTO) {
        return new PurchaseModel(purchaseDTO.getId(),
                purchaseDTO.getIsPaid(),
                VehicleModel.DTOtoModel(purchaseDTO.getVehicle()),
                purchaseDTO.getPurchaseDate());
    }
}
