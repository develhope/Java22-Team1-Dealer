package com.develhope.spring.Purchase.Entities.DTO;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.order.Entities.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDTO {

    private Long id;

    private int deposit;

    private boolean isPaid;

    private PurchaseStatus status;

    private Order order;


    public PurchaseDTO(int deposit, boolean isPaid, PurchaseStatus status, Order order) {
        this.deposit = deposit;
        this.isPaid = isPaid;
        this.status = status;
        this.order = order;
    }

    public PurchaseModel toModel() {
        return new PurchaseModel(this.deposit, this.isPaid, this.status, this.order);
    }
}