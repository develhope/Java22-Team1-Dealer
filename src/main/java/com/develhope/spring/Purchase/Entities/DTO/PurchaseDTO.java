package com.develhope.spring.Purchase.Entities.DTO;

import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PurchaseDTO {
    private Long id;

    private Boolean isPaid;

    private VehicleDTO vehicle;

    private LocalDate purchaseDate;

}