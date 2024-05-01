package com.develhope.spring.Rent.Request;

import com.develhope.spring.Rent.Entities.Enums.RentStatus;
import lombok.Data;

@Data
public class RentRequest {

    private double deposit;

    private boolean isPaid;

    private RentStatus status;

}
