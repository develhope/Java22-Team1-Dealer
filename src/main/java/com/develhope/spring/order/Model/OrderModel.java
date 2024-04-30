package com.develhope.spring.order.Model;

import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.Entities.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderModel {
    private Long orderId;

    private int deposit;

    private boolean paid;

    private String status;

    private boolean isSold;

    private User user;

    private List<PurchaseEntity> purchases;

    public OrderModel(int deposit, boolean paid, String status, boolean isSold, User user, List<PurchaseEntity> purchases) {
        this.deposit = deposit;
        this.paid = paid;
        this.status = status;
        this.isSold = isSold;
        this.user = user;
        this.purchases = purchases;
    }

    public static OrderEntity modelToEntity(OrderModel orderModel) {
        return new OrderEntity(orderModel.getOrderId(), orderModel.getDeposit(), orderModel.isPaid(),
                orderModel.getStatus(), orderModel.isSold(), orderModel.getUser(), orderModel.getPurchases());
    }
    public static OrderDTO modelToDto(OrderModel orderModel) {
        return new OrderDTO(orderModel.getOrderId(), orderModel.getDeposit(), orderModel.isPaid(),
                orderModel.getStatus(), orderModel.isSold(), orderModel.getUser(), orderModel.getPurchases());
    }

    public static OrderModel entityToModel(OrderEntity orderEntity) {
        return new OrderModel(orderEntity.getOrderId(), orderEntity.getDeposit(), orderEntity.isPaid(),
                orderEntity.getStatus(), orderEntity.isSold(), orderEntity.getUser(), orderEntity.getPurchases());
    }

    public static OrderModel dtoToModel(OrderDTO orderDTO) {
        return new OrderModel(orderDTO.getOrderId(), orderDTO.getDeposit(), orderDTO.isPaid(),
                orderDTO.getStatus(), orderDTO.isSold(), orderDTO.getUser(), orderDTO.getPurchases());
    }
}
