package com.develhope.spring.Purchase.Model;

import com.develhope.spring.Purchase.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.order.Entities.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseModel {
    private Long id;

    private double deposit;

    private boolean isPaid;

    private PurchaseStatus status;

    private Order order;


    public PurchaseModel(double deposit, boolean isPaid, PurchaseStatus status, Order order) {
        this.deposit = deposit;
        this.isPaid = isPaid;
        this.status = status;
        this.order = order;
    }

    public static PurchaseEntity modelToEntity(PurchaseModel purchaseModel) {
        return new PurchaseEntity(purchaseModel.getId(), purchaseModel.getDeposit(), purchaseModel.isPaid(), purchaseModel.getStatus(), purchaseModel.getOrder());
    }
    public static PurchaseDTO modelToDto(PurchaseModel purchaseModel) {
        return new PurchaseDTO(purchaseModel.getId(), purchaseModel.getDeposit(), purchaseModel.isPaid(), purchaseModel.getStatus(), purchaseModel.getOrder());
    }

    public static PurchaseModel entityToModel(PurchaseEntity purchaseEntity) {
        return new PurchaseModel(purchaseEntity.getId(),purchaseEntity.getDeposit(), purchaseEntity.isPaid(), purchaseEntity.getStatus(), purchaseEntity.getOrder());
    }

    public static PurchaseModel dtoToModel(PurchaseDTO purchaseDTO) {
        return new PurchaseModel(purchaseDTO.getId(),purchaseDTO.getDeposit(), purchaseDTO.isPaid(), purchaseDTO.getStatus(), purchaseDTO.getOrder());
    }
}