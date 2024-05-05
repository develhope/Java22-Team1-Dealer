package com.develhope.spring.Rent.Request;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RentRequest {

    private LocalDate startDate;
    private LocalDate endDate;
    private Double dailyCost;
    private boolean paid;
    private VehicleEntity vehicleId;

}
