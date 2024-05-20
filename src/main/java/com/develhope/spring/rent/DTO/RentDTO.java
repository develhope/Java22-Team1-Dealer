package com.develhope.spring.rent.DTO;

import com.develhope.spring.vehicles.entities.VehicleEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

//da visionare non so se corretto
@Data
public class RentDTO {

    private LocalDate startDate; // data inizio nol

    private LocalDate endDate; // data fine nol

    private BigDecimal dailyCost; // costo girnaliero nol

    private Boolean isPaid; // stato pagamento nol

    private BigDecimal totalCost;

    private VehicleEntity vehicleEntity;
    private Long id;

    public RentDTO(LocalDate startDate, LocalDate endDate, BigDecimal dailyCost, Boolean isPaid, VehicleEntity vehicleEntity, BigDecimal totalCost, Long id) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.dailyCost = dailyCost;
        this.vehicleEntity = vehicleEntity;
        this.totalCost = totalCost;
        this.id = id;
    }

}
