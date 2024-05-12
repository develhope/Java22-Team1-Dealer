package com.develhope.spring.order.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
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
    VehicleRepository vehicleRepository;
    @Autowired
    UserRepository userRepository;

    public Either<OrderResponse, OrderDTO> create(UserEntity buyer, OrderRequest orderRequest) {
        if(orderRequest == null) {
            return Either.left(new OrderResponse(400, "Invalid input parameters"));

        }

        Optional<VehicleEntity> foundVehicle = vehicleRepository.findById(orderRequest.getVehicleId());
        if (foundVehicle.isEmpty()) {
            return Either.left(new OrderResponse(404, "Vehicle not found"));
        }
        if (foundVehicle.get().getVehicleStatus() != VehicleStatus.NOT_AVAILABLE) {
            return Either.left(new OrderResponse(403, "Vehicle is not orderable"));
        }

        OrderModel orderModel = new OrderModel(orderRequest.getDeposit(), orderRequest.getPaid(), orderRequest.getStatus(), orderRequest.getIsSold(),
                buyer,
                foundVehicle.get());
        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(orderModel));
        OrderModel savedModel = OrderModel.entityToModel(savedEntity);
        return Either.right(OrderModel.modelToDto(savedModel));
    }

    public Either<OrderResponse, OrderDTO> getSingle(UserEntity userEntity, Long orderId) {
        Optional<OrderEntity> orderEntityOptional = orderRepository.findById(orderId);
        if (orderEntityOptional.isEmpty()) {
            return Either.left(new OrderResponse(404, "Order with id" + orderId + " not found"));
        }
        if(userEntity.getUserType() != UserTypes.ADMIN) {
            OrderEntity orderEntity = orderEntityOptional.get();
            if (userEntity.getOrderEntities().stream().noneMatch(oe -> oe.getOrderId().equals(orderEntity.getOrderId()))) {
                return Either.left(new OrderResponse(404, "Order does not belong to specified user"));
            }
        }

        OrderModel orderModel = OrderModel.entityToModel(orderEntityOptional.get());
        return Either.right(OrderModel.modelToDto(orderModel));
    }

    private Either<OrderResponse, OrderDTO> getSingleById(Long targetId, Long orderId) {
        Optional<UserEntity> targetUser = userRepository.findById(targetId);
        if (targetUser.isEmpty()) {
            return Either.left(new OrderResponse(404, "User with id " + targetId + " was not found"));
        }
        return getSingle(targetUser.get(), orderId);
    }

    public Either<OrderResponse, List<OrderDTO>> getAll(UserEntity user) {
        List<OrderEntity> userOrders = user.getOrderEntities();
        if (userOrders.isEmpty()) {
            return Either.left(new OrderResponse(404, "No orders found for the user " + user.getId()));
        }

        return Either.right(userOrders.stream().map(orderEntity -> {
            OrderModel orderModel = OrderModel.entityToModel(orderEntity);
            return OrderModel.modelToDto(orderModel);
        }).toList());
    }

    public Either<OrderResponse, OrderDTO> update(UserEntity userEntity, Long target, Long orderId, OrderRequest orderRequest) {
        if (orderId == null || orderRequest == null) {
            return Either.left(new OrderResponse(400, "Invalid input parameters"));
        }

        Either<OrderResponse, OrderDTO> foundOrder = target != null ? getSingleById(target, orderId) : getSingle(userEntity, orderId);
        if (foundOrder.isLeft()) {
            return foundOrder;
        }

        Optional<VehicleEntity> vehicleEntity = Optional.empty();
        if(orderRequest.getVehicleId() != null) {
             vehicleEntity = vehicleRepository.findById(orderRequest.getVehicleId());
        }

        foundOrder.get().setDeposit(orderRequest.getDeposit() == null ? foundOrder.get().getDeposit() : orderRequest.getDeposit());
        foundOrder.get().setPaid(orderRequest.getPaid() == null ? foundOrder.get().getPaid() : orderRequest.getPaid());
        foundOrder.get().setStatus(orderRequest.getStatus() == null ? foundOrder.get().getStatus() : orderRequest.getStatus());
        foundOrder.get().setIsSold(orderRequest.getIsSold() == null ? foundOrder.get().getIsSold() : orderRequest.getIsSold());
        foundOrder.get().setVehicleEntity(vehicleEntity.orElseGet(() -> foundOrder.get().getVehicleEntity()));

        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(OrderModel.dtoToModel(foundOrder.get())));

        OrderModel savedModel = OrderModel.entityToModel(savedEntity);
        return Either.right(OrderModel.modelToDto(savedModel));
    }

    public OrderResponse deleteOrder(UserEntity userEntity, Long orderId) {
        if (orderId == null) {
            return new OrderResponse(400, "Invalid input parameters");
        }
        Either<OrderResponse, OrderDTO> singleOrderResult = getSingle(userEntity, orderId);
        if (singleOrderResult.isLeft()) {
            return singleOrderResult.getLeft();
        }

        Optional<OrderEntity> orderEntity = orderRepository.findById(orderId);

        try {
            orderRepository.delete(orderEntity.get());
            return new OrderResponse(200, "Order deleted successfully");
        } catch (Exception e) {
            return new OrderResponse(500, "Internal server error");
        }
    }
}
