package com.develhope.spring.Purchase.Entities.DTO;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseModel {
    private Long id;

    private Double deposit;

    private Boolean isPaid;

    private PurchaseStatus status;

    private VehicleEntity vehicleEntity;


    public PurchaseModel(Double deposit, Boolean isPaid, PurchaseStatus status, VehicleEntity vehicleEntity) {
        this.deposit = deposit;
        this.isPaid = isPaid;
        this.status = status;
        this.vehicleEntity = vehicleEntity;
    }

    public static PurchaseEntity modelToEntity(PurchaseModel purchaseModel) {
        return new PurchaseEntity(purchaseModel.getId(),
                purchaseModel.getDeposit(),
                purchaseModel.getIsPaid(),
                purchaseModel.getStatus(),
                purchaseModel.getVehicleEntity());
    }
    public static PurchaseDTO modelToDto(PurchaseModel purchaseModel) {
        return new PurchaseDTO(purchaseModel.getId(),
                purchaseModel.getDeposit(),
                purchaseModel.getIsPaid(),
                purchaseModel.getStatus(),
                purchaseModel.getVehicleEntity());
    }

    public static PurchaseModel entityToModel(PurchaseEntity purchaseEntity) {
        return new PurchaseModel(purchaseEntity.getPurchaseId(),
                purchaseEntity.getDeposit(),
                purchaseEntity.getIsPaid(),
                purchaseEntity.getStatus(),
                purchaseEntity.getVehicleEntity());
    }

    public static PurchaseModel dtoToModel(PurchaseDTO purchaseDTO) {
        return new PurchaseModel(purchaseDTO.getId(),
                purchaseDTO.getDeposit(),
                purchaseDTO.getIsPaid(),
                purchaseDTO.getStatus(),
                purchaseDTO.getVehicleEntity());
    }
}
