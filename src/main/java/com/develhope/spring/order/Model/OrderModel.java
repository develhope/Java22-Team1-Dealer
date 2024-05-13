package com.develhope.spring.order.Model;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.Entities.OrderEntity;
import com.develhope.spring.order.Entities.OrdersLink;
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

    private VehicleEntity vehicleEntity;

    private OrdersLink ordersLink;


    public OrderModel(Integer deposit, Boolean paid, String status, Boolean isSold, VehicleEntity vehicleEntity) {
        this.deposit = deposit;
        this.paid = paid;
        this.status = status;
        this.isSold = isSold;
        this.vehicleEntity = vehicleEntity;
    }

    public static OrderEntity modelToEntity(OrderModel orderModel) {
        return new OrderEntity(
                orderModel.getOrderId(),
                orderModel.getDeposit(),
                orderModel.getPaid(),
                orderModel.getStatus(),
                orderModel.getIsSold(),
                orderModel.getVehicleEntity(),
                orderModel.getOrdersLink()
        );
    }

    public static OrderDTO modelToDto(OrderModel orderModel) {
        return new OrderDTO(
                orderModel.getOrderId(),
                orderModel.getDeposit(),
                orderModel.getPaid(),
                orderModel.getStatus(),
                orderModel.getIsSold(),
                orderModel.getVehicleEntity(),
                orderModel.getOrdersLink());
    }

    public static OrderModel entityToModel(OrderEntity orderEntity) {
        return new OrderModel(
                orderEntity.getOrderId(),
                orderEntity.getDeposit(),
                orderEntity.getIsPaid(),
                orderEntity.getStatus(),
                orderEntity.getIsSold(),
                orderEntity.getVehicleEntity(),
                orderEntity.getOrdersLink()
        );
    }


    public static OrderModel dtoToModel(OrderDTO orderDTO) {
        return new OrderModel(orderDTO.getOrderId(),
                orderDTO.getDeposit(),
                orderDTO.getPaid(),
                orderDTO.getStatus(),
                orderDTO.getIsSold(),
                orderDTO.getVehicleEntity(),
                orderDTO.getOrdersLink());
    }
}
