package com.develhope.spring.Rent.DTO;

import lombok.Data;
import java.time.LocalDate;

//da visionare non so se corretto
@Data
public class RentDTO {
    private LocalDate startDate; // data inizio nol
    private LocalDate endDate; // data fine nol
    private Double dailyCost; // costo girnaliero nol
    private Boolean isPaid; // stato pagamento nol
    private Long vehicleId; // id del veicolo del noleggio
}
