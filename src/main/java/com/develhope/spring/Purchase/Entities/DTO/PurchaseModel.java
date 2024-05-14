package com.develhope.spring.Purchase.Entities.DTO;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleModel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseModel {
    private Long id;

    private Double deposit;

    private Boolean isPaid;

    private PurchaseStatus status;

    private VehicleModel vehicle;


    public PurchaseModel(Double deposit, Boolean isPaid, PurchaseStatus status, VehicleModel vehicle) {
        this.deposit = deposit;
        this.isPaid = isPaid;
        this.status = status;
        this.vehicle = vehicle;
    }

    public static PurchaseEntity modelToEntity(PurchaseModel purchaseModel) {
        return new PurchaseEntity(purchaseModel.getId(),
                purchaseModel.getDeposit(),
                purchaseModel.getIsPaid(),
                purchaseModel.getStatus(),
                VehicleModel.modelToEntity(purchaseModel.getVehicle()));
    }
    public static PurchaseDTO modelToDto(PurchaseModel purchaseModel) {
        return new PurchaseDTO(purchaseModel.getId(),
                purchaseModel.getDeposit(),
                purchaseModel.getIsPaid(),
                purchaseModel.getStatus(),
                VehicleModel.modelToDTO(purchaseModel.getVehicle()));
    }

    public static PurchaseModel entityToModel(PurchaseEntity purchaseEntity) {
        return new PurchaseModel(purchaseEntity.getPurchaseId(),
                purchaseEntity.getDeposit(),
                purchaseEntity.getIsPaid(),
                purchaseEntity.getStatus(),
                VehicleModel.entityToModel(purchaseEntity.getVehicle()));
    }

    public static PurchaseModel dtoToModel(PurchaseDTO purchaseDTO) {
        return new PurchaseModel(purchaseDTO.getId(),
                purchaseDTO.getDeposit(),
                purchaseDTO.getIsPaid(),
                purchaseDTO.getStatus(),
                VehicleModel.DTOtoModel(purchaseDTO.getVehicle()));
    }
}
