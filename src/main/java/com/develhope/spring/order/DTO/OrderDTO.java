package com.develhope.spring.order.DTO;

import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
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

    private VehicleDTO vehicle;
}
