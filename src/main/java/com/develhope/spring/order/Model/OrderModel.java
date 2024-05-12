package com.develhope.spring.order.Model;

import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Entities.UserModel;
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

    private UserEntity buyer;

    private VehicleEntity vehicleEntity;


    public OrderModel(Integer deposit, Boolean paid, String status, Boolean isSold, UserEntity buyer, VehicleEntity vehicleEntity) {
        this.deposit = deposit;
        this.paid = paid;
        this.status = status;
        this.isSold = isSold;
        this.buyer = buyer;
        this.vehicleEntity = vehicleEntity;
    }

    public static OrderEntity modelToEntity(OrderModel orderModel) {
        return new OrderEntity(
                orderModel.getOrderId(),
                orderModel.getDeposit(),
                orderModel.getPaid(),
                orderModel.getStatus(),
                orderModel.getIsSold(),
                orderModel.getBuyer(),
                orderModel.getVehicleEntity()
        );
    }

    public static OrderDTO modelToDto(OrderModel orderModel) {
        return new OrderDTO(
                orderModel.getOrderId(),
                orderModel.getDeposit(),
                orderModel.getPaid(),
                orderModel.getStatus(),
                orderModel.getIsSold(),
                UserModel.modelToDtoWithoutList(UserModel.entityToModel(orderModel.getBuyer())),
                orderModel.getVehicleEntity()
        );
    }

    public static OrderModel entityToModel(OrderEntity orderEntity) {
        return new OrderModel(
                orderEntity.getOrderId(),
                orderEntity.getDeposit(),
                orderEntity.getIsPaid(),
                orderEntity.getStatus(),
                orderEntity.getIsSold(),
                orderEntity.getOrderBuyer(),
                orderEntity.getVehicleEntity()
        );
    }


    public static OrderModel dtoToModel(OrderDTO orderDTO) {
        return new OrderModel(orderDTO.getOrderId(),
                orderDTO.getDeposit(),
                orderDTO.getPaid(),
                orderDTO.getStatus(),
                orderDTO.getIsSold(),
                UserModel.modelToEntity(UserModel.dtoToModel(orderDTO.getUser())),
                orderDTO.getVehicleEntity());
    }
}
