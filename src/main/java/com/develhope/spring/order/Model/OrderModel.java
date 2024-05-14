package com.develhope.spring.order.Model;

import com.develhope.spring.Vehicles.Entities.DTO.VehicleModel;
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

    private VehicleModel vehicle;

    public OrderModel(Integer deposit, Boolean paid, String status, Boolean isSold, VehicleModel vehicle) {
        this.deposit = deposit;
        this.paid = paid;
        this.status = status;
        this.isSold = isSold;
        this.vehicle = vehicle;
    }

    public static OrderEntity modelToEntity(OrderModel orderModel) {
        return new OrderEntity(
                orderModel.getOrderId(),
                orderModel.getDeposit(),
                orderModel.getPaid(),
                orderModel.getStatus(),
                orderModel.getIsSold(),
                VehicleModel.modelToEntity(orderModel.getVehicle())
        );
    }

    public static OrderDTO modelToDto(OrderModel orderModel) {
        return new OrderDTO(
                orderModel.getOrderId(),
                orderModel.getDeposit(),
                orderModel.getPaid(),
                orderModel.getStatus(),
                orderModel.getIsSold(),
                VehicleModel.modelToDTO(orderModel.getVehicle()));
    }

    public static OrderModel entityToModel(OrderEntity orderEntity) {
        return new OrderModel(
                orderEntity.getOrderId(),
                orderEntity.getDeposit(),
                orderEntity.getIsPaid(),
                orderEntity.getStatus(),
                orderEntity.getIsSold(),
                VehicleModel.entityToModel(orderEntity.getVehicle())
        );
    }

    public static OrderModel dtoToModel(OrderDTO orderDTO) {
        return new OrderModel(orderDTO.getOrderId(),
                orderDTO.getDeposit(),
                orderDTO.getPaid(),
                orderDTO.getStatus(),
                orderDTO.getIsSold(),
                VehicleModel.DTOtoModel(orderDTO.getVehicle()));
    }
}
