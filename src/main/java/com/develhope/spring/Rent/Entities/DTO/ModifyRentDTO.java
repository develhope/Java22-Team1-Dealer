package com.develhope.spring.Rent.Entities.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ModifyRentDTO {
    private LocalDate startDate; //nuovs data inizio nol
    private LocalDate endDate; //nuova data fine nol
}
