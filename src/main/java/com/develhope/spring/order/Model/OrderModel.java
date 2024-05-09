package com.develhope.spring.order.Model;

import com.develhope.spring.User.Entities.User;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.Entities.OrderEntity;
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

    private User buyer;

    private VehicleEntity vehicleEntity;


    public OrderModel(int deposit, boolean paid, String status, boolean isSold, User buyer, VehicleEntity vehicleEntity) {
        this.deposit = deposit;
        this.paid = paid;
        this.status = status;
        this.isSold = isSold;
        this.buyer = buyer;
        this.vehicleEntity = vehicleEntity;
    }

    public static OrderEntity modelToEntity(OrderModel orderModel) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderModel.getOrderId());
        orderEntity.setDeposit(orderModel.getDeposit());
        orderEntity.setPaid(orderModel.isPaid());
        orderEntity.setStatus(orderModel.getStatus());
        orderEntity.setSold(orderModel.isSold());
        orderEntity.setBuyer(orderModel.getBuyer());
        return orderEntity;
    }

    public static OrderDTO modelToDto(OrderModel orderModel) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderModel.getOrderId());
        orderDTO.setDeposit(orderModel.getDeposit());
        orderDTO.setPaid(orderModel.isPaid());
        orderDTO.setStatus(orderModel.getStatus());
        orderDTO.setSold(orderModel.isSold());
        orderDTO.setUser(orderModel.getBuyer());

        return orderDTO;
    }

    public static OrderModel entityToModel(OrderEntity orderEntity) {
        OrderModel orderModel = new OrderModel();
        orderModel.setOrderId(orderEntity.getOrderId());
        orderModel.setDeposit(orderEntity.getDeposit());
        orderModel.setPaid(orderEntity.isPaid());
        orderModel.setStatus(orderEntity.getStatus());
        orderModel.setSold(orderEntity.isSold());
        orderModel.setBuyer(orderEntity.getBuyer());

        return orderModel;
    }

    public static OrderModel dtoToModel(OrderDTO orderDTO) {
        OrderModel orderModel = new OrderModel();
        orderModel.setOrderId(orderDTO.getOrderId());
        orderModel.setDeposit(orderDTO.getDeposit());
        orderModel.setPaid(orderDTO.isPaid());
        orderModel.setStatus(orderDTO.getStatus());
        orderModel.setSold(orderDTO.isSold());
        orderModel.setBuyer(orderDTO.getUser());
        return orderModel;
    }
}
