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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        assertTrue(result.isRight());

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository, times(1)).saveAndFlush(orderCaptor.capture());
        assertEquals(orderRequest.getDeposit(), orderCaptor.getValue().getDeposit());

        ArgumentCaptor<VehicleEntity> vehicleCaptor = ArgumentCaptor.forClass(VehicleEntity.class);
        verify(vehicleRepository, times(1)).save(vehicleCaptor.capture());
        assertEquals(VehicleStatus.ORDERED, vehicleCaptor.getValue().getVehicleStatus());

        verify(ordersLinkRepository, times(1)).save(any(OrdersLinkEntity.class));
    }

    @Test
    public void testCreateOrderNonExistentVehicle() {
        UserEntity seller = new UserEntity(1L, "pietro", "tornasole", "+31",
                "culobello@si.of", "igattini", UserTypes.SELLER);

        UserEntity buyer = new UserEntity(2L, "carlo", "santini", "+22", "sis", "icagnolini", UserTypes.BUYER);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setVehicleId(99L);
        orderRequest.setDeposit(BigDecimal.valueOf(100));
        orderRequest.setPaid(false);
        orderRequest.setStatus("ORDERED");

        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        Either<OrderResponse, OrderDTO> result = orderService.create(seller, buyer.getId(), orderRequest);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
    }

    @Test
    public void testCreateOrderNonOrderableVehicle() {
        UserEntity seller = new UserEntity(1L, "pietro", "tornasole", "+31",
                "culobello@si.of", "igattini", UserTypes.SELLER);

        UserEntity buyer = new UserEntity(2L, "carlo", "santini", "+22", "sis", "icagnolini", UserTypes.BUYER);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setVehicleId(3L);
        orderRequest.setDeposit(BigDecimal.valueOf(100));
        orderRequest.setPaid(false);
        orderRequest.setStatus("ORDERED");

        VehicleEntity vehicle = new VehicleEntity(3L, "Lamborghini", "Revuelto",  6, "Blue", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.SOLD, VehicleType.CAR); // Non-orderable status

        when(vehicleRepository.findById(3L)).thenReturn(Optional.of(vehicle));

        Either<OrderResponse, OrderDTO> result = orderService.create(seller, buyer.getId(), orderRequest);

        assertTrue(result.isLeft());
        assertEquals(403, result.getLeft().getCode());
    }

    @Test
    public void testCreateOrderInvalidInput() {
        Either<OrderResponse, OrderDTO> result = orderService.create(new UserEntity(), null, null);
        System.out.println(result);

        assertTrue(result.isLeft());
        assertEquals(400, result.getLeft().getCode());
    }

    @Test
    public void testCreateOrderInvalidDeposit() {
        UserEntity seller = new UserEntity(1L, "pietro", "tornasole", "+31",
                "culobello@si.of", "igattini", UserTypes.SELLER);

        UserEntity buyer = new UserEntity(2L, "carlo", "santini", "+22", "sis", "icagnolini", UserTypes.BUYER);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setVehicleId(3L);
        orderRequest.setDeposit(BigDecimal.valueOf(-10));

        Either<OrderResponse, OrderDTO> result = orderService.create(seller, buyer.getId(), orderRequest);

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
    public void testGetSingleUnauthorizedUser() {
        UserEntity notAuthorized = new UserEntity();
        notAuthorized.setId(2L);
        notAuthorized.setUserType(UserTypes.BUYER);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(1L);

        OrdersLinkEntity ordersLink = new OrdersLinkEntity();
        ordersLink.setBuyer(userEntity);
        ordersLink.setOrder(orderEntity);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(ordersLink);

        Either<OrderResponse, OrderDTO> result = orderService.getSingle(notAuthorized, 1L);

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
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setStatus("ORDERED");

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(null);

        Either<OrderResponse, OrderDTO> result = orderService.update(userEntity, 1L, orderRequest);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
    }
    @Test
    public void testUpdateOrderInvalidOrderRequest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        Either<OrderResponse, OrderDTO> result = orderService.update(userEntity, 1L, null);

        assertTrue(result.isLeft());
        assertEquals(400, result.getLeft().getCode());
    }

    @Test
    public void testUpdateUnauthorizedUser() {
        UserEntity notAuthorized = new UserEntity();
        notAuthorized.setId(2L);
        notAuthorized.setUserType(UserTypes.BUYER);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(1L);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setStatus("Ordered");

        OrdersLinkEntity ordersLink = new OrdersLinkEntity();
        ordersLink.setBuyer(userEntity);
        ordersLink.setOrder(orderEntity);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(ordersLink);

        Either<OrderResponse, OrderDTO> result = orderService.update(notAuthorized, 1L, orderRequest);

        assertTrue(result.isLeft());
        assertEquals(404, result.getLeft().getCode());
    }

    @Test
    public void testUpdateOrderInvalidOrderId() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setStatus("ORDERED");

        Either<OrderResponse, OrderDTO> result = orderService.update(userEntity, null, orderRequest);
        System.out.println(result);

        assertTrue(result.isLeft());
        assertEquals(400, result.getLeft().getCode());
    }

    @Test
    public void testUpdateOrderSuccess() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.BUYER);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setPaid(true);

        OrdersLinkEntity ordersLink = new OrdersLinkEntity();
        ordersLink.setBuyer(userEntity);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(1L);
        orderEntity.setIsPaid(false);
        orderEntity.setVehicle(new VehicleEntity(3L, "Lamborghini", "Revuelto",  6, "Blue", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.ORDERABLE, VehicleType.CAR));

        ordersLink.setOrder(orderEntity);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(ordersLink);
        when(orderRepository.saveAndFlush(any(OrderEntity.class))).thenReturn(orderEntity);

        Either<OrderResponse, OrderDTO> result = orderService.update(userEntity, 1L, orderRequest);

        assertTrue(result.isRight());

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository, times(1)).saveAndFlush(orderCaptor.capture());
        assertTrue(orderCaptor.getValue().getIsPaid());
    }

    @Test
    public void testDeleteOrderNotFound() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(null);

        OrderResponse result = orderService.deleteOrder(userEntity, 1L);

        assertEquals(404, result.getCode());
    }

    @Test
    public void testDeleteOrderSuccess() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUserType(UserTypes.BUYER);

        OrdersLinkEntity ordersLink = new OrdersLinkEntity();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(1L);
        orderEntity.setVehicle(new VehicleEntity(3L, "Lamborghini", "Revuelto",  6, "Blue", 1015,
                "Automatic", 2021, "PHEV / Gasoline", BigDecimal.valueOf(517255),
                BigDecimal.valueOf(1), Collections.singletonList("Air Conditioning"), true, VehicleStatus.ORDERABLE, VehicleType.CAR));

        ordersLink.setOrder(orderEntity);
        ordersLink.setBuyer(userEntity);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(ordersLink);

        OrderResponse result = orderService.deleteOrder(userEntity, 1L);

        assertEquals(200, result.getCode());
        verify(ordersLinkRepository, times(1)).delete(any(OrdersLinkEntity.class));

        ArgumentCaptor<VehicleEntity> vehicleCaptor = ArgumentCaptor.forClass(VehicleEntity.class);
        verify(vehicleRepository, times(1)).save(vehicleCaptor.capture());
        assertEquals(VehicleStatus.ORDERABLE, vehicleCaptor.getValue().getVehicleStatus());

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        assertEquals(OrderStatus.CANCELED, orderCaptor.getValue().getStatus());
    }

    @Test
    public void testDeleteUnauthorizedUser() {
        UserEntity notAuthorized = new UserEntity();
        notAuthorized.setId(2L);
        notAuthorized.setUserType(UserTypes.BUYER);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(1L);

        OrdersLinkEntity ordersLink = new OrdersLinkEntity();
        ordersLink.setBuyer(userEntity);
        ordersLink.setOrder(orderEntity);

        when(ordersLinkRepository.findByOrder_OrderId(1L)).thenReturn(ordersLink);

        OrderResponse result = orderService.deleteOrder(notAuthorized, 1L);

        assertEquals(404, result.getCode());
        assertEquals("Order with id " + 1L + " not found or does not belong to specified user", result.getMessage());
    }
    @Test
    public void testDeleteOrderInvalidOrderId() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        OrderResponse result = orderService.deleteOrder(userEntity, null);

        assertEquals(400, result.getCode());
    }
}