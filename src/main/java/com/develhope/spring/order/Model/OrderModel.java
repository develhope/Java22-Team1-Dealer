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

    private Integer deposit;

    private Boolean paid;

    private String status;

    private Boolean isSold;

    private User buyer;

    private VehicleEntity vehicleEntity;


    public OrderModel(Integer deposit, Boolean paid, String status, Boolean isSold, User buyer, VehicleEntity vehicleEntity) {
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
        orderEntity.setIsPaid(orderModel.getPaid());
        orderEntity.setStatus(orderModel.getStatus());
        orderEntity.setIsSold(orderModel.getIsSold());
        orderEntity.setOrderBuyer(orderModel.getBuyer());
        return orderEntity;
    }

    public static OrderDTO modelToDto(OrderModel orderModel) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(orderModel.getOrderId());
        orderDTO.setDeposit(orderModel.getDeposit());
        orderDTO.setPaid(orderModel.getPaid());
        orderDTO.setStatus(orderModel.getStatus());
        orderDTO.setIsSold(orderModel.getIsSold());
        orderDTO.setUser(orderModel.getBuyer());

        return orderDTO;
    }

    public static OrderModel entityToModel(OrderEntity orderEntity) {
        OrderModel orderModel = new OrderModel();
        orderModel.setOrderId(orderEntity.getOrderId());
        orderModel.setDeposit(orderEntity.getDeposit());
        orderModel.setPaid(orderEntity.getIsPaid());
        orderModel.setStatus(orderEntity.getStatus());
        orderModel.setIsSold(orderEntity.getIsSold());
        orderModel.setBuyer(orderEntity.getOrderBuyer());

        return orderModel;
    }

    public static OrderModel dtoToModel(OrderDTO orderDTO) {
        OrderModel orderModel = new OrderModel();
        orderModel.setOrderId(orderDTO.getOrderId());
        orderModel.setDeposit(orderDTO.getDeposit());
        orderModel.setPaid(orderDTO.getPaid());
        orderModel.setStatus(orderDTO.getStatus());
        orderModel.setIsSold(orderDTO.getIsSold());
        orderModel.setBuyer(orderDTO.getUser());
        return orderModel;
    }
}
