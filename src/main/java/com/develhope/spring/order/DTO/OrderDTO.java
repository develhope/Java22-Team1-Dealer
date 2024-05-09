package com.develhope.spring.order.DTO;

import com.develhope.spring.User.Entities.User;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;

    private Integer deposit;

    private Boolean paid;

    private String status;

    private Boolean isSold;

    private User user;

    private VehicleEntity vehicleEntity;

}
