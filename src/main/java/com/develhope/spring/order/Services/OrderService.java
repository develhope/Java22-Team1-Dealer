package com.develhope.spring.order.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
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

    public Either<OrderResponse, OrderDTO> create(Long userId, boolean isAdmin, OrderRequest orderRequest) {
        //check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new OrderResponse(404, "User with id " + userId + " not found"));
        }
        User user = userOptional.get();

        // check authorization
        if (!isAdmin && user.getUserType() == UserTypes.SELLER) {
            return Either.left(new OrderResponse(403, "User cannot see orders"));
        }

        if (isAdmin && user.getUserType() != UserTypes.ADMIN) {
            return Either.left(new OrderResponse(403, "User is not an admin"));
        }

        OrderModel orderModel = new OrderModel(orderRequest.getDeposit(), orderRequest.isPaid(), orderRequest.getStatus(),
                orderRequest.isSold(), orderRequest.getUser(), orderRequest.getPurchases());

        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(orderModel));

        OrderModel savedModel = OrderModel.entityToModel(savedEntity);
        return Either.right(OrderModel.modelToDto(savedModel));
    }

    public Either<OrderResponse, OrderDTO> getSingle(Long userId, Long orderId, boolean isAdmin) {
        //check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new OrderResponse(404, "User with id" + userId + " not found"));
        }
        if (isAdmin && userOptional.get().getUserType() != UserTypes.ADMIN) {
            return Either.left(new OrderResponse(403, "User is not an admin"));
        }
        //check if order exists
        Optional<OrderEntity> orderEntityOptional = orderRepository.findById(orderId);
        if (orderEntityOptional.isEmpty()) {
            return Either.left(new OrderResponse(404, "Order with id" + orderId + " not found"));
        }

        //check if order belongs to specified user
        User user = userOptional.get();
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

    public Either<OrderResponse, OrderDTO> update(Long userId, Long orderId, boolean isAdmin, OrderRequest orderRequest) {
        Either<OrderResponse, OrderDTO> foundOrder = getSingle(userId, orderId, isAdmin);
        if (foundOrder.isLeft()) {
            return foundOrder;
        }

        OrderModel orderModel = new OrderModel(orderRequest.getDeposit(), orderRequest.isPaid(), orderRequest.getStatus(),
                orderRequest.isSold(), orderRequest.getUser(), orderRequest.getPurchases());

        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(orderModel));

        OrderModel savedModel = OrderModel.entityToModel(savedEntity);
        return Either.right(OrderModel.modelToDto(savedModel));
    }

    public OrderResponse deleteOrder(Long userId, Long orderId, boolean isAdmin) {
        //checks if purchase and user exists and they belong to each other
        Either<OrderResponse, OrderDTO> singlePurchaseResult = getSingle(userId, orderId, isAdmin);
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
