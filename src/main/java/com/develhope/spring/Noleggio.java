package com.develhope.spring;

//Per un noleggio avremo:
//Data inizio noleggio
//Data fine noleggio
//Costo giornaliero noleggio
//Costo totale noleggio
//Flag pagato
//Veicolo noleggiato

import java.time.LocalDate;

public class Noleggio {

    private Long id;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private double costoGiornaliero;
    private double costoTotale;
    private boolean pagato;
    private Veicolo veicoloNoleggiato; //da unire con la sua classe
    private Cliente cliente; //da unire con la sua classe

    public Noleggio(LocalDate dataInizio, LocalDate dataFine, double costoGiornaliero, Veicolo veicoloNoleggiato, Cliente cliente) {
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.costoGiornaliero = costoGiornaliero;
        this.veicoloNoleggiato = veicoloNoleggiato;
        this.cliente = cliente;
        this.calcolaCostoTotale();
    }

    private void calcolaCostoTotale() {
        long giorni = java.time.temporal.ChronoUnit.DAYS.between(dataInizio, dataFine);
        this.costoTotale = giorni * costoGiornaliero;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public double getCostoGiornaliero() {
        return costoGiornaliero;
    }

    public void setCostoGiornaliero(double costoGiornaliero) {
        this.costoGiornaliero = costoGiornaliero;
    }

    public double getCostoTotale() {
        return costoTotale;
    }

    public void setCostoTotale(double costoTotale) {
        this.costoTotale = costoTotale;
    }

    public boolean isPagato() {
        return pagato;
    }

    public void setPagato(boolean pagato) {
        this.pagato = pagato;
    }

    public Veicolo getVeicoloNoleggiato() {
        return veicoloNoleggiato;
    }

    public void setVeicoloNoleggiato(Veicolo veicoloNoleggiato) {
        this.veicoloNoleggiato = veicoloNoleggiato;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
