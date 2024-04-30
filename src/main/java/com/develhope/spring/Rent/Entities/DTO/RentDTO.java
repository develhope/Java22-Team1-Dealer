package com.develhope.spring.Rent.Entities.DTO;

import lombok.Data;
import java.time.LocalDate;

//da visionare non so se corretto
@Data
public class RentDTO {

    private LocalDate startDate; // data inizio nol

    private LocalDate endDate; // data fine nol

    private Double dailyCost; // costo girnaliero nol

    private Boolean isPaid; // stato pagamento nol

    private Long vehicleId;

    public RentDTO(LocalDate startDate, LocalDate endDate, Double dailyCost, Boolean isPaid, Long vehicleId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.dailyCost = dailyCost;
        this.vehicleId = vehicleId;
    }

}
