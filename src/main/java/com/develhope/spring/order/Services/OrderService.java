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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    @Transactional
    public Either<OrderResponse, OrderDTO> create(UserEntity seller, @Nullable Long buyerId, OrderRequest orderRequest) {
        if (orderRequest == null || orderRequest.getDeposit().intValue() < 0) {
            return Either.left(new OrderResponse(400, "Invalid input parameters"));
        }

        VehicleEntity foundVehicle = vehicleRepository.findById(orderRequest.getVehicleId()).orElse(null);
        if (foundVehicle == null || foundVehicle.getVehicleStatus() != VehicleStatus.NOT_AVAILABLE) {
            return Either.left(new OrderResponse(foundVehicle == null ? 404 : 403, foundVehicle == null ? "Specified vehicle not found" : "Vehicle is not orderable"));
        }

        UserEntity buyer = resolveWhosBuyer(seller, buyerId);
        if (buyer == null) {
            return Either.left(new OrderResponse(404, "Specified buyer not found"));
        }

        VehicleModel newVehicle = createVehicleFromExistingOne(foundVehicle);

        VehicleEntity savedVehicle = vehicleRepository.save(VehicleModel.modelToEntity(newVehicle));
        OrderModel orderModel = new OrderModel(orderRequest.getDeposit(), orderRequest.getPaid(), OrderStatus.convertFromString(orderRequest.getStatus()),
                VehicleModel.entityToModel(savedVehicle)
                , LocalDate.now());

        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(orderModel));
        ordersLinkRepository.save(new OrdersLinkEntity(buyer, savedEntity, seller));

        OrderModel savedModel = OrderModel.entityToModel(savedEntity);
        return Either.right(OrderModel.modelToDto(savedModel));
    }

    private VehicleModel createVehicleFromExistingOne(VehicleEntity vehicleEntity) {
        return new VehicleModel(vehicleEntity.getBrand(),
                vehicleEntity.getModel(),
                vehicleEntity.getDisplacement(),
                vehicleEntity.getColor(),
                vehicleEntity.getPower(),
                vehicleEntity.getTransmission(),
                vehicleEntity.getRegistrationYear(),
                vehicleEntity.getPowerSupply(),
                vehicleEntity.getPrice(),
                vehicleEntity.getDiscount(),
                vehicleEntity.getAccessories(),
                vehicleEntity.getIsNew(),
                VehicleStatus.ORDERED,
                vehicleEntity.getVehicleType());
    }

    private UserEntity resolveWhosBuyer(UserEntity seller, @Nullable Long buyerId) {
        if (buyerId != null && (seller.getUserType() == UserTypes.ADMIN || seller.getUserType() == UserTypes.SELLER)) {
            return userRepository.findById(buyerId).orElse(null);
        }
        return seller;
    }

    public Either<OrderResponse, OrderDTO> getSingle(UserEntity userEntity, Long orderId) {
        OrdersLinkEntity ordersLink = ordersLinkRepository.findByOrder_OrderId(orderId);
        if (ordersLink == null || (!userEntity.getUserType().equals(UserTypes.ADMIN) && !ordersLink.getBuyer().getId().equals(userEntity.getId()))) {
            return Either.left(new OrderResponse(404, "Order with id " + orderId + " not found or does not belong to specified user"));
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

    @Transactional
    public Either<OrderResponse, OrderDTO> update(UserEntity user, Long orderId, OrderRequest orderRequest) {
        if (orderId == null || orderRequest == null) {
            return Either.left(new OrderResponse(400, "Invalid input parameters"));
        }

        Either<OrderResponse, OrderDTO> foundOrder = getSingle(user, orderId);

        if (foundOrder.isLeft()) {
            return Either.left(foundOrder.getLeft());
        }

        OrderDTO orderDTO = foundOrder.get();
        updateOrderDetails(orderDTO, orderRequest);

        OrderEntity savedEntity = orderRepository.saveAndFlush(OrderModel.modelToEntity(OrderModel.dtoToModel(orderDTO)));
        OrderModel savedModel = OrderModel.entityToModel(savedEntity);

        if (savedModel.getStatus() == OrderStatus.DELIVERED) {
            updateVehicleStatus(savedEntity.getVehicle(), VehicleStatus.SOLD);
        }

        return Either.right(OrderModel.modelToDto(savedModel));
    }

    private void updateOrderDetails(OrderDTO orderDTO, OrderRequest orderRequest) {
        orderDTO.setDeposit(orderRequest.getDeposit() == null ? orderDTO.getDeposit() : orderRequest.getDeposit());
        orderDTO.setPaid(orderRequest.getPaid() == null ? orderDTO.getPaid() : orderRequest.getPaid());
        orderDTO.setStatus(orderRequest.getStatus() == null ? orderDTO.getStatus() : OrderStatus.convertFromString(orderRequest.getStatus()));
        orderDTO.setVehicle(orderRequest.getVehicleId() != null ?
                vehicleRepository.findById(orderRequest.getVehicleId()).map(
                        vehicleEntity -> VehicleModel.modelToDTO(VehicleModel.entityToModel(vehicleEntity))
                ).orElse(orderDTO.getVehicle()) : orderDTO.getVehicle());
    }

    private VehicleEntity updateVehicleStatus(VehicleEntity vehicle, VehicleStatus vehicleStatus) {
        vehicle.setVehicleStatus(vehicleStatus);
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public OrderResponse deleteOrder(UserEntity userEntity, Long orderId) {
        if (orderId == null) {
            return new OrderResponse(400, "Invalid input parameters");
        }
        Either<OrderResponse, OrderDTO> singleOrderResult = getSingle(userEntity, orderId);
        if (singleOrderResult.isLeft()) {
            return singleOrderResult.getLeft();
        }
        try {
            OrderModel orderModel = OrderModel.dtoToModel(singleOrderResult.get());
            orderModel.setStatus(OrderStatus.CANCELED);

            updateVehicleStatus(VehicleModel.modelToEntity(orderModel.getVehicle()), VehicleStatus.NOT_AVAILABLE);

            orderModel.setVehicle(null);
            orderRepository.save(OrderModel.modelToEntity(orderModel));

            ordersLinkRepository.delete(ordersLinkRepository.findByOrder_OrderId(orderId));
            return new OrderResponse(200, "Order deleted successfully");
        } catch (Exception e) {
            return new OrderResponse(500, e.getMessage());
        }
    }
}
