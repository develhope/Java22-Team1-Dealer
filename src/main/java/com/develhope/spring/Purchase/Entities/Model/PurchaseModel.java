package com.develhope.spring.Purchase.Entities.Model;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.Entity.PurchaseEntity;
import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.order.Entities.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseModel {
    private Long id;

    private int deposit;

    private boolean isPaid;

    private PurchaseStatus status;

    private Order order;


    public PurchaseModel(int deposit, boolean isPaid, PurchaseStatus status, Order order) {
        this.deposit = deposit;
        this.isPaid = isPaid;
        this.status = status;
        this.order = order;
    }

    public PurchaseEntity toEntity() {
        return new PurchaseEntity(this.id, this.deposit, this.isPaid, this.status, this.order);
    }
    public PurchaseDTO toDto() {
        return new PurchaseDTO(this.deposit, this.isPaid, this.status, this.order);
    }
}
