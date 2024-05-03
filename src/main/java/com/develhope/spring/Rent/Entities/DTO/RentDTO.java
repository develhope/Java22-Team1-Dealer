package com.develhope.spring.Rent.Entities.DTO;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import lombok.Data;
import java.time.LocalDate;

//da visionare non so se corretto
@Data
public class RentDTO {

    private LocalDate startDate; // data inizio nol

    private LocalDate endDate; // data fine nol

    private Double dailyCost; // costo girnaliero nol

    private Boolean isPaid; // stato pagamento nol

    private VehicleEntity vehicleEntity;

    public RentDTO(LocalDate startDate, LocalDate endDate, Double dailyCost, Boolean isPaid, Long vehicleEntity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.dailyCost = dailyCost;
        this.vehicleEntity = vehicleEntity;
    }

}
