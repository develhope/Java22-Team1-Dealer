package com.develhope.spring.Purchase.DTO;

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

    private double deposit;

    private boolean isPaid;

    private PurchaseStatus status;

    private Order order;

}