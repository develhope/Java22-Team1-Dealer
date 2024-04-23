package com.develhope.spring;

//Per un noleggio avremo:
//Data inizio noleggio
//Data fine noleggio
//Costo giornaliero noleggio
//Costo totale noleggio
//Flag pagato
//Veicolo noleggiato

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Noleggio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "Inizio noleggio")
    private LocalDate dataInizio;
    @Column(nullable = false, name = "Fine noleggio")
    private LocalDate dataFine;
    @Column(nullable = false, name = "Costo giornaliero")
    private Double costoGiornaliero;
    @Column(nullable = false, name = "Costo totale")
    private Double costoTotale;
    @Column(nullable = false, name = "Pagato")
    private Boolean pagato;
    @Column(nullable = false, name = "Veicolo")
    private Veicolo veicoloNoleggiato; //da unire con la sua classe
    @Column(nullable = false, name = "Cliente")
    private Cliente cliente; //da unire con la sua classe


    private void calcolaCostoTotale() {
        long giorni = java.time.temporal.ChronoUnit.DAYS.between(dataInizio, dataFine);
        this.costoTotale = giorni * costoGiornaliero;
    }

}
