package com.develhope.spring;

import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.entities.OrderEntity;
import com.develhope.spring.order.entities.OrdersLinkEntity;
import com.develhope.spring.order.entities.enums.OrderStatus;
import com.develhope.spring.order.orderRequest.OrderRequest;
import com.develhope.spring.order.repositories.OrderRepository;
import com.develhope.spring.order.repositories.OrdersLinkRepository;
import com.develhope.spring.order.response.OrderResponse;
import com.develhope.spring.order.service.OrderService;
import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.entities.VehicleType;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrdersLinkRepository ordersLinkRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrderSuccess() {
        UserEntity seller = new UserEntity(1L, "pietro", "tornasole", "+31",
                "culobello@si.of", "igattini", UserTypes.SELLER);

        UserEntity buyer = new UserEntity(2L, "carlo", "santini", "+22", "sis", "icagnolini", UserTypes.BUYER);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setVehicleId(3L);
        orderRequest.setDeposit(BigDecimal.valueOf(100));
        orderRequest.setPaid(false);
        orderRequest.setStatus("ORDERED");

        VehicleEntity vehicle= new VehicleEntity(3L, "Lamborghini", "Revuelto",  6, "Blue", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.ORDERABLE, VehicleType.CAR);
        OrderEntity orderEntity = new OrderEntity(1L, orderRequest.getDeposit(),
                orderRequest.getPaid(),
                OrderStatus.convertFromString(orderRequest.getStatus()),
                vehicle,
                LocalDate.now());

        when(vehicleRepository.findById(3L)).thenReturn(Optional.of(vehicle));
        when(userRepository.findById(buyer.getId())).thenReturn(Optional.of(buyer));
        when(orderRepository.saveAndFlush(any(OrderEntity.class))).thenReturn(orderEntity);
        when(ordersLinkRepository.save(any(OrdersLinkEntity.class))).thenReturn(new OrdersLinkEntity());


        Either<OrderResponse, OrderDTO> result = orderService.create(seller, buyer.getId(), orderRequest);
        System.out.println(result);

        assertTrue(result.isRight());
        verify(vehicleRepository, times(1)).save(vehicle);
        verify(orderRepository, times(1)).saveAndFlush(any(OrderEntity.class));
        verify(ordersLinkRepository, times(1)).save(any(OrdersLinkEntity.class));
    }

    @Test
    public void testCreateOrderInvalidInput() {
        Either<OrderResponse, OrderDTO> result = orderService.create(new UserEntity(), null, null);
        System.out.println(result);

        assertTrue(result.isLeft());
        assertEquals(400, result.getLeft().getCode());
    }

    @Test
    public void testGetSingleOrderNotFound() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(null);

        Either<OrderResponse, OrderDTO> result = orderService.getSingle(userEntity, 1L);
        System.out.println(result);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
    }

    @Test
    public void testGetSingleOrderSuccess() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.BUYER);

        OrdersLinkEntity ordersLink = new OrdersLinkEntity();
        ordersLink.setBuyer(userEntity);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(1L);
        orderEntity.setVehicle(new VehicleEntity(3L, "Lamborghini", "Revuelto",  6, "Blue", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.ORDERABLE, VehicleType.CAR));
        ordersLink.setOrder(orderEntity);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(ordersLink);

        Either<OrderResponse, OrderDTO> result = orderService.getSingle(userEntity, 1L);

        assertTrue(result.isRight());
        assertEquals(1L, result.get().getOrderId());
    }

    @Test
    public void testGetAllOrdersNotFound() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(ordersLinkRepository.findByBuyer_Id(1L)).thenReturn(Collections.emptyList());

        Either<OrderResponse, List<OrderDTO>> result = orderService.getAll(userEntity);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
    }

    @Test
    public void testGetAllOrdersSuccess() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrdersLinkEntity ordersLink = new OrdersLinkEntity();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(1L);
        orderEntity.setVehicle(new VehicleEntity(3L, "Lamborghini", "Revuelto",  6, "Blue", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.ORDERABLE, VehicleType.CAR));

        ordersLink.setOrder(orderEntity);

        when(ordersLinkRepository.findByBuyer_Id(1L)).thenReturn(List.of(ordersLink));

        Either<OrderResponse, List<OrderDTO>> result = orderService.getAll(userEntity);

        assertTrue(result.isRight());
        assertEquals(1, result.get().size());
        assertEquals(1L, result.get().get(0).getOrderId());
    }

    @Test
    public void testUpdateOrderNotFound() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setStatus("ORDERED");

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(null);

        // Act
        Either<OrderResponse, OrderDTO> result = orderService.update(userEntity, 1L, orderRequest);

        // Assert
        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
    }

    @Test
    public void testUpdateOrderSuccess() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.BUYER);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setPaid(false);

        OrdersLinkEntity ordersLink = new OrdersLinkEntity();
        ordersLink.setBuyer(userEntity);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(1L);
        ordersLink.setOrder(orderEntity);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(ordersLink);
        when(orderRepository.saveAndFlush(any(OrderEntity.class))).thenReturn(orderEntity);

        // Act
        Either<OrderResponse, OrderDTO> result = orderService.update(userEntity, 1L, orderRequest);

        // Assert
        assertTrue(result.isRight());
        assertEquals(1L, result.get().getOrderId());
    }

    @Test
    public void testDeleteOrderNotFound() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(null);

        // Act
        OrderResponse result = orderService.deleteOrder(userEntity, 1L);

        // Assert
        assertEquals(404, result.getCode());
    }

    @Test
    public void testDeleteOrderSuccess() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrdersLinkEntity ordersLink = new OrdersLinkEntity();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(1L);
        ordersLink.setOrder(orderEntity);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(ordersLink);

        // Act
        OrderResponse result = orderService.deleteOrder(userEntity, 1L);

        // Assert
        assertEquals(200, result.getCode());
        verify(ordersLinkRepository, times(1)).delete(any(OrdersLinkEntity.class));
        verify(vehicleRepository, times(1)).save(any(VehicleEntity.class));
    }
}