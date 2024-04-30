package com.develhope.spring.order.Services;

import com.develhope.spring.Order.DTO.OrderDTO;
import com.develhope.spring.Order.Entities.OrderEntity;
import com.develhope.spring.Order.Model.OrderModel;
import com.develhope.spring.Order.Request.OrderRequest;
import com.develhope.spring.Order.Response.OrderResponse;
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

import java.util.Optional;

@Service
public class OrderService {
    @Autowired
     OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    
    public Either<OrderResponse, OrderDTO> create(Long userId, OrderRequest orderRequest) {
        //check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new OrderResponse(404, "User not found"));
        }


    }

    public Either<OrderResponse, OrderDTO> createByAdmin(Long adminId, Long userId, OrderRequest orderRequest) {
        //check if admin exists
        Optional<User> adminOptional = userRepository.findById(userId);
        if (adminOptional.isEmpty()) {
            return Either.left(new OrderResponse(404, "Admin with id" + adminId + " not found"));
        }
        //checks if requesting user is an admin
        if(adminOptional.get().getUserType() != UserTypes.ADMIN) {
            return Either.left(new OrderResponse(403, "User with id " + adminId + " is not an admin"));
        }
        //check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new OrderResponse(404, "User with id" + userId + " not found"));
        }


        OrderModel orderModel = new OrderModel(orderRequest.getDeposit(), orderRequest.isPaid(), orderRequest.getStatus(),
                orderRequest.isSold(), orderRequest.getUser(), orderRequest.getPurchases());

        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(orderModel));

        OrderModel savedModel = OrderModel.entityToModel(savedEntity);
        return Either.right(OrderModel.modelToDto(savedModel));
    }
}
