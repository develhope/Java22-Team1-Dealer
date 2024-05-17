package com.develhope.spring.order.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleModel;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.Entities.OrderEntity;
import com.develhope.spring.order.Entities.OrdersLinkEntity;
import com.develhope.spring.order.Entities.enums.OrderStatus;
import com.develhope.spring.order.Model.OrderModel;
import com.develhope.spring.order.OrderRequest.OrderRequest;
import com.develhope.spring.order.Repositories.OrderRepository;
import com.develhope.spring.order.Repositories.OrdersLinkRepository;
import com.develhope.spring.order.Response.OrderResponse;
import io.vavr.control.Either;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Autowired
    OrdersLinkRepository ordersLinkRepository;

    public Either<OrderResponse, OrderDTO> create(UserEntity seller, @Nullable Long buyerId, OrderRequest orderRequest) {
        if (orderRequest == null || orderRequest.getDeposit().intValue() < 0) {
            return Either.left(new OrderResponse(400, "Invalid input parameters"));
        }

        Optional<VehicleEntity> foundVehicle = vehicleRepository.findById(orderRequest.getVehicleId());
        if (foundVehicle.isEmpty()) {
            return Either.left(new OrderResponse(404, "Vehicle not found"));
        }
        if (foundVehicle.get().getVehicleStatus() != VehicleStatus.NOT_AVAILABLE) {
            return Either.left(new OrderResponse(403, "Vehicle is not orderable"));
        }

        UserEntity buyer;
        if (buyerId != null && seller.getUserType() == UserTypes.ADMIN || seller.getUserType() == UserTypes.SELLER) {
            Optional<UserEntity> optionalBuyer = userRepository.findById(buyerId);
            if (optionalBuyer.isEmpty()) {
                return Either.left(new OrderResponse(404, "Specified buyer not found"));
            }
            buyer = optionalBuyer.get();
        } else {
            buyer = seller;
        }
        OrderModel orderModel = new OrderModel(orderRequest.getDeposit(), orderRequest.getPaid(), OrderStatus.convertFromString(orderRequest.getStatus()),
                VehicleModel.entityToModel(foundVehicle.get()), LocalDate.now());
        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(orderModel));
        if (!buyer.getId().equals(seller.getId())) {
            ordersLinkRepository.saveAndFlush(new OrdersLinkEntity(buyer, savedEntity, seller));
        } else {
            ordersLinkRepository.saveAndFlush(new OrdersLinkEntity(buyer, savedEntity));
        }

        OrderModel savedModel = OrderModel.entityToModel(savedEntity);
        return Either.right(OrderModel.modelToDto(savedModel));
    }

    public Either<OrderResponse, OrderDTO> getSingle(UserEntity userEntity, Long orderId) {
        OrdersLinkEntity ordersLink = ordersLinkRepository.findByOrder_OrderId(orderId);
        if (ordersLink == null) {
            return Either.left(new OrderResponse(404, "Order with id " + orderId + " not found"));
        }

        if (userEntity.getUserType() != UserTypes.ADMIN) {
            OrderEntity orderEntity = ordersLink.getOrder();
            List<OrderEntity> userOrders = ordersLinkRepository.findByBuyer_Id(userEntity.getId()).stream().map(OrdersLinkEntity::getOrder).toList();
            if (userOrders.stream().noneMatch(oe -> oe.getOrderId().equals(orderEntity.getOrderId()))) {
                return Either.left(new OrderResponse(404, "Order does not belong to specified user"));
            }
        }

        OrderModel orderModel = OrderModel.entityToModel(ordersLink.getOrder());
        return Either.right(OrderModel.modelToDto(orderModel));
    }

    public Either<OrderResponse, List<OrderDTO>> getAll(UserEntity user) {
        List<OrderEntity> userOrders = ordersLinkRepository.findByBuyer_Id(user.getId()).stream().map(OrdersLinkEntity::getOrder).toList();
        if (userOrders.isEmpty()) {
            return Either.left(new OrderResponse(404, "No orders found for the user " + user.getId()));
        }

        return Either.right(userOrders.stream().map(orderEntity -> {
            OrderModel orderModel = OrderModel.entityToModel(orderEntity);
            return OrderModel.modelToDto(orderModel);
        }).toList());
    }

    public Either<OrderResponse, OrderDTO> update(UserEntity user, Long orderId, OrderRequest orderRequest) {
        if (orderId == null || orderRequest == null) {
            return Either.left(new OrderResponse(400, "Invalid input parameters"));
        }

        Either<OrderResponse, OrderDTO> foundOrder = getSingle(user, orderId);

        if (foundOrder.isLeft()) {
            return Either.left(foundOrder.getLeft());
        }

        Optional<VehicleEntity> vehicleEntity = Optional.empty();
        if (orderRequest.getVehicleId() != null) {
            vehicleEntity = vehicleRepository.findById(orderRequest.getVehicleId());
        }

        foundOrder.get().setDeposit(orderRequest.getDeposit() == null ? foundOrder.get().getDeposit() : orderRequest.getDeposit());
        foundOrder.get().setPaid(orderRequest.getPaid() == null ? foundOrder.get().getPaid() : orderRequest.getPaid());
        foundOrder.get().setStatus(orderRequest.getStatus() == null ? foundOrder.get().getStatus() : OrderStatus.convertFromString(orderRequest.getStatus()));
        foundOrder.get().setVehicle(vehicleEntity.map(entity -> VehicleModel.modelToDTO(VehicleModel.entityToModel(entity))).orElseGet(() -> foundOrder.get().getVehicle()));
        if(foundOrder.get().getStatus() == OrderStatus.DELIVERED) {
          VehicleModel newVehicle =  new VehicleModel(foundOrder.get().getVehicle().getBrand(),
                    foundOrder.get().getVehicle().getModel(),
                    foundOrder.get().getVehicle().getDisplacement(),
                    foundOrder.get().getVehicle().getColor(),
                    foundOrder.get().getVehicle().getPower(),
                    foundOrder.get().getVehicle().getTransmission(),
                    foundOrder.get().getVehicle().getRegistrationYear(),
                    foundOrder.get().getVehicle().getPowerSupply(),
                    foundOrder.get().getVehicle().getPrice(),
                    foundOrder.get().getVehicle().getDiscount(),
                    foundOrder.get().getVehicle().getAccessories(),
                    foundOrder.get().getVehicle().getIsNew(),
                    VehicleStatus.SOLD,
                    foundOrder.get().getVehicle().getVehicleType());

            vehicleRepository.save(VehicleModel.modelToEntity(newVehicle));
        }

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

        try {
            ordersLinkRepository.delete(ordersLinkRepository.findByOrder_OrderId(orderId));
            return new OrderResponse(200, "Order deleted successfully");
        } catch (Exception e) {
            return new OrderResponse(500, e.getMessage());
        }
    }
}
