package com.develhope.spring.order.model;

import com.develhope.spring.vehicles.model.VehicleModel;
import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.entities.OrderEntity;
import com.develhope.spring.order.entities.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {
    private Long orderId;

    private BigDecimal deposit;

    private Boolean paid;

    private OrderStatus status;

    private VehicleModel vehicle;
    private LocalDate orderDate;


    public OrderModel(BigDecimal deposit, Boolean paid, OrderStatus status, VehicleModel vehicle, LocalDate orderDate) {
        this.deposit = deposit;
        this.paid = paid;
        this.status = status;
        this.vehicle = vehicle;
        this.orderDate = orderDate;
    }

    public static OrderEntity modelToEntity(OrderModel orderModel) {
        return new OrderEntity(
                orderModel.getOrderId(),
                orderModel.getDeposit(),
                orderModel.getPaid(),
                orderModel.getStatus(),
                VehicleModel.modelToEntity(orderModel.getVehicle()),
                orderModel.getOrderDate()
        );
    }

    public static OrderDTO modelToDto(OrderModel orderModel) {
        return new OrderDTO(
                orderModel.getOrderId(),
                orderModel.getDeposit(),
                orderModel.getPaid(),
                orderModel.getStatus(),
                VehicleModel.modelToDTO(orderModel.getVehicle()),
                orderModel.getOrderDate());
    }

    public static OrderModel entityToModel(OrderEntity orderEntity) {
        return new OrderModel(
                orderEntity.getOrderId(),
                orderEntity.getDeposit(),
                orderEntity.getIsPaid(),
                orderEntity.getStatus(),
                VehicleModel.entityToModel(orderEntity.getVehicle()),
                orderEntity.getOrderDate());
    }

    public static OrderModel dtoToModel(OrderDTO orderDTO) {
        return new OrderModel(orderDTO.getOrderId(),
                orderDTO.getDeposit(),
                orderDTO.getPaid(),
                orderDTO.getStatus(),
                VehicleModel.DTOtoModel(orderDTO.getVehicle()),
                orderDTO.getOrderDate());
    }
}
