package com.develhope.spring.order.Model;

import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.Entities.OrderEntity;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {
    private Long orderId;

    private int deposit;

    private boolean paid;

    private String status;

    private boolean isSold;

    private User user;

    @Nullable
    private PurchaseEntity purchase;
    @Nullable
    private RentEntity rent;
    @Nullable
    private User intermediary;

    public OrderModel(int deposit, boolean paid, String status, boolean isSold, User user, @Nullable PurchaseEntity purchase, @Nullable RentEntity rent, @Nullable User intermediary) {
        this.deposit = deposit;
        this.paid = paid;
        this.status = status;
        this.isSold = isSold;
        this.user = user;
        this.purchase = purchase;
        this.rent = rent;
        this.intermediary = intermediary;
    }

    public OrderModel(int deposit, boolean paid, String status, boolean isSold, User user, @Nullable PurchaseEntity purchase) {
        this.deposit = deposit;
        this.paid = paid;
        this.status = status;
        this.isSold = isSold;
        this.user = user;
        this.purchase = purchase;
    }

    public static OrderEntity modelToEntity(OrderModel orderModel) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderModel.getOrderId());
        orderEntity.setDeposit(orderModel.getDeposit());
        orderEntity.setPaid(orderModel.isPaid());
        orderEntity.setStatus(orderModel.getStatus());
        orderEntity.setSold(orderModel.isSold());
        orderEntity.setUser(orderModel.getUser());

        if (orderModel.getPurchase() != null) {
            orderEntity.setPurchase(orderModel.getPurchase());
        }
        if (orderModel.getRent() != null) {
            orderEntity.setRent(orderModel.getRent());
        }

        return orderEntity;
    }

    public static OrderDTO modelToDto(OrderModel orderModel) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderModel.getOrderId());
        orderDTO.setDeposit(orderModel.getDeposit());
        orderDTO.setPaid(orderModel.isPaid());
        orderDTO.setStatus(orderModel.getStatus());
        orderDTO.setSold(orderModel.isSold());
        orderDTO.setUser(orderModel.getUser());
        orderDTO.setIntermediary(orderModel.getIntermediary());

        if (orderModel.getPurchase() != null) {
            orderDTO.setPurchase(orderModel.getPurchase());
        }
        if (orderModel.getRent() != null) {
            orderDTO.setRent(orderModel.getRent());
        }
        return orderDTO;
    }

    public static OrderModel entityToModel(OrderEntity orderEntity) {
        OrderModel orderModel = new OrderModel();
        orderModel.setOrderId(orderEntity.getOrderId());
        orderModel.setDeposit(orderEntity.getDeposit());
        orderModel.setPaid(orderEntity.isPaid());
        orderModel.setStatus(orderEntity.getStatus());
        orderModel.setSold(orderEntity.isSold());
        orderModel.setUser(orderEntity.getUser());

        // Se è impostato l'attributo 'purchase', converte in 'PurchaseEntity'
        if (orderEntity.getPurchase() != null) {
            orderModel.setPurchase(orderEntity.getPurchase());
        }
        // Se è impostato l'attributo 'rent', converte in 'RentEntity'
        if (orderEntity.getRent() != null) {
            orderModel.setRent(orderEntity.getRent());
        }

        return orderModel;
    }

    public static OrderModel dtoToModel(OrderDTO orderDTO) {
        OrderModel orderModel = new OrderModel();
        orderModel.setOrderId(orderDTO.getOrderId());
        orderModel.setDeposit(orderDTO.getDeposit());
        orderModel.setPaid(orderDTO.isPaid());
        orderModel.setStatus(orderDTO.getStatus());
        orderModel.setSold(orderDTO.isSold());
        orderModel.setUser(orderDTO.getUser());

        if (orderDTO.getPurchase() != null) {
            orderModel.setPurchase(orderDTO.getPurchase());
        }
        if (orderDTO.getRent() != null) {
            orderModel.setRent(orderDTO.getRent());
        }

        return orderModel;
    }
}
