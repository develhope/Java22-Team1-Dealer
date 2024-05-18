package com.develhope.spring.order.DTO;

import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import com.develhope.spring.order.Entities.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;

    private BigDecimal deposit;

    private Boolean paid;

    private OrderStatus status;

    private VehicleDTO vehicle;

    private LocalDate orderDate;

}
