package com.develhope.spring.order.Services;

import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.Entities.OrderEntity;
import com.develhope.spring.order.Model.OrderModel;
import com.develhope.spring.order.OrderRequest.OrderRequest;
import com.develhope.spring.order.Repositories.OrderRepository;
import com.develhope.spring.order.Response.OrderResponse;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VehicleRepository vehicleRepository;

    public Either<OrderResponse, OrderDTO> create(User buyer, OrderRequest orderRequest) {
        Optional<VehicleEntity> foundVehicle = vehicleRepository.findById(orderRequest.getVehicleId());
        if(foundVehicle.isEmpty()) {
            return Either.left(new OrderResponse(404, "Vehicle not found"));
        }
        if(foundVehicle.get().getVehicleStatus() != VehicleStatus.NOT_AVAILABLE) {
            return Either.left(new OrderResponse(403, "Vehicle is not orderable"));
        }

        OrderModel orderModel = new OrderModel(orderRequest.getDeposit(), orderRequest.getPaid(), orderRequest.getStatus(), orderRequest.getIsSold(), buyer, foundVehicle.get());
        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(orderModel));
        OrderModel savedModel = OrderModel.entityToModel(savedEntity);
        return Either.right(OrderModel.modelToDto(savedModel));
    }

    public Either<OrderResponse, OrderDTO> getSingle(User user, Long orderId) {
        Optional<OrderEntity> orderEntityOptional = orderRepository.findById(orderId);
        if (orderEntityOptional.isEmpty()) {
            return Either.left(new OrderResponse(404, "Order with id" + orderId + " not found"));
        }

        //check if order belongs to specified user
        OrderEntity orderEntity = orderEntityOptional.get();
        if (!(user.getOrderEntities().contains(orderEntity))) {
            return Either.left(new OrderResponse(403, "This order does not belong to specified user"));
        }

        OrderModel orderModel = OrderModel.entityToModel(orderEntity);
        return Either.right(OrderModel.modelToDto(orderModel));
    }

    public Either<OrderResponse, List<OrderDTO>> getAll(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new OrderResponse(404, "User with id" + userId + " not found"));
        }

        List<OrderEntity> userOrders = userOptional.get().getOrderEntities();
        if (userOrders.isEmpty()) {
            return Either.left(new OrderResponse(404, "No orders found for the user " + userId));
        }

        return Either.right(userOrders.stream().map(orderEntity -> {
            OrderModel orderModel = OrderModel.entityToModel(orderEntity);
            return OrderModel.modelToDto(orderModel);
        }).toList());
    }

    public Either<OrderResponse, OrderDTO> update(User user, Long orderId, OrderRequest orderRequest) {
        Either<OrderResponse, OrderDTO> foundOrder = getSingle(user, orderId);
        if (foundOrder.isLeft()) {
            return foundOrder;
        }
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(orderRequest.getVehicleId());

        foundOrder.get().setDeposit(orderRequest.getDeposit() == null ? foundOrder.get().getDeposit() : orderRequest.getDeposit());
        foundOrder.get().setPaid(orderRequest.getPaid() == null ? foundOrder.get().getPaid() : orderRequest.getPaid());
        foundOrder.get().setStatus(orderRequest.getStatus() == null ? foundOrder.get().getStatus() : orderRequest.getStatus());
        foundOrder.get().setIsSold(orderRequest.getIsSold() == null ? foundOrder.get().getIsSold() : orderRequest.getIsSold());
        foundOrder.get().setVehicleEntity(vehicleEntity.orElseGet(() -> foundOrder.get().getVehicleEntity()));

        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(OrderModel.dtoToModel(foundOrder.get())));

        OrderModel savedModel = OrderModel.entityToModel(savedEntity);
        return Either.right(OrderModel.modelToDto(savedModel));
    }

    public OrderResponse deleteOrder(User user, Long orderId) {
        Either<OrderResponse, OrderDTO> singlePurchaseResult = getSingle(user, orderId);
        if (singlePurchaseResult.isLeft()) {
            return singlePurchaseResult.getLeft();
        }

        Optional<OrderEntity> purchaseEntity = orderRepository.findById(orderId);

        try {
            orderRepository.delete(purchaseEntity.get());
            return new OrderResponse(200, "Purchase deleted successfully");
        } catch (Exception e) {
            return new OrderResponse(500, "Internal server error");
        }
    }
}
