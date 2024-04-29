package com.develhope.spring.Rent.Entities.DTO;

import com.develhope.spring.Rent.Entities.RentEntity;
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

    public RentModel toModel() {
        RentModel rentModel = new RentModel();
        rentModel.setStartDate(this.startDate);
        rentModel.setEndDate(this.endDate);
        rentModel.setDailyCost(this.dailyCost);
        rentModel.setIsPaid(this.isPaid);
        rentModel.setVehicleId(this.vehicleId);
        return rentModel;
    }
}
