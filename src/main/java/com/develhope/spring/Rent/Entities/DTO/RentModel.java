package com.develhope.spring.Rent.Entities.DTO;

import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RentModel {
    private LocalDate startDate; // data inizio nol

    private LocalDate endDate; // data fine nol

    private Double dailyCost; // costo girnaliero nol

    private Boolean isPaid; // stato pagamento nol

    private VehicleEntity vehicleEntity;


    public RentModel(LocalDate startDate, LocalDate endDate, Double dailyCost, Boolean isPaid, VehicleEntity vehicleEntity) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.isPaid = isPaid;
        this.dailyCost = dailyCost;
        this.vehicleEntity = vehicleEntity;
    }

    public static RentEntity modelToEntity(RentModel rentModel) {
        return new RentEntity(rentModel.getStartDate(), rentModel.getEndDate(), rentModel.getDailyCost(), rentModel.getIsPaid(), rentModel.getVehicleEntity());
    }
    public static RentDTO modelToDTO(RentModel rentModel) {
        return new RentDTO(rentModel.getStartDate(), rentModel.getEndDate(), rentModel.getDailyCost(), rentModel.getIsPaid(), rentModel.getVehicleEntity());
    }

    public static RentModel dtoToModel(RentDTO rentDTO) {
        return new RentModel(rentDTO.getStartDate(), rentDTO.getEndDate(), rentDTO.getDailyCost(), rentDTO.getIsPaid(), rentDTO.getVehicleEntity());
    }

    public static RentModel entityToModel(RentEntity rentEntity) {
        return new RentModel(rentEntity.getStartDate(), rentEntity.getEndDate(), rentEntity.getDailyCost(), rentEntity.getIsPaid(), rentEntity.getVehicleId());
    }
}
