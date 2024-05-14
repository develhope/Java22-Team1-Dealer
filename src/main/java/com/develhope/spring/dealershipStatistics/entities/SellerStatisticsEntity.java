package com.develhope.spring.dealershipStatistics.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class SellerStatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;
    private int numberOfOrders;
    private int numberOfSales;
    private int numberOfRentals;

    // Costruttore vuoto necessario per JPA
    public SellerStatisticsEntity() {
    }

    // Costruttore con parametri
    public SellerStatisticsEntity(Long sellerId, int numberOfOrders, int numberOfSales, int numberOfRentals) {
        this.sellerId = sellerId;
        this.numberOfOrders = numberOfOrders;
        this.numberOfSales = numberOfSales;
        this.numberOfRentals = numberOfRentals;
    }

    // Getter e setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public int getNumberOfSales() {
        return numberOfSales;
    }

    public void setNumberOfSales(int numberOfSales) {
        this.numberOfSales = numberOfSales;
    }

    public int getNumberOfRentals() {
        return numberOfRentals;
    }

    public void setNumberOfRentals(int numberOfRentals) {
        this.numberOfRentals = numberOfRentals;
    }
}
