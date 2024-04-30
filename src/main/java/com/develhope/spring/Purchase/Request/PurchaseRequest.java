package com.develhope.spring.Purchase.Request;

import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.order.Entities.Order;
import lombok.Data;

@Data
public class PurchaseRequest {

    private double deposit;

    private boolean isPaid;

    private PurchaseStatus status;

    private Order order;

}
