package com.develhope.spring.Rent.Entities.DTO;

import com.develhope.spring.Rent.Entities.RentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentModel {
    private LocalDate startDate; // data inizio nol

    private LocalDate endDate; // data fine nol

    private Double dailyCost; // costo girnaliero nol

    private Boolean isPaid; // stato pagamento nol

    private Long vehicleId;


    public RentModel(LocalDate startDate, LocalDate endDate, Double dailyCost, Boolean isPaid) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.dailyCost = dailyCost;
    }

    public RentEntity toEntity() {
        RentEntity rentEntity = new RentEntity();
        rentEntity.setStartDate(this.startDate);
        rentEntity.setEndDate(this.endDate);
        rentEntity.setDailyCost(this.dailyCost);
        rentEntity.setIsPaid(this.isPaid);
        rentEntity.setTotalCost(rentEntity.calculateTotalCost());
        return rentEntity;
    }
    public RentDTO toDTO() {
        return new RentDTO(this.startDate, this.endDate, this.dailyCost, this.isPaid, this.vehicleId);
    }
}
