package com.develhope.spring.order.DTO;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.order.Entities.OrdersLink;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderDTO {

    private Long orderId;

    private Integer deposit;

    private Boolean paid;

    private String status;

    private Boolean isSold;

    private VehicleEntity vehicleEntity;

    private OrdersLink ordersLink;
}
