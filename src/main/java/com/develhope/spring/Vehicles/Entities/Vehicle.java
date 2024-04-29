package com.develhope.spring.Vehicles.Entities;

import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @Column(nullable = false, name = "Brand")
    private String brand;

    @Column(nullable = false, name = "Model")
    private String model;

    @Column(nullable = false, name = "Displacement")
    private Integer displacement;

    @Column(nullable = false, name = "Color")
    private String color;

    @Column(nullable = false, name = "Power")
    private Integer power;

    @Column(nullable = false, name = "Transmission")
    private String transmission;

    @Column(nullable = false, name = "Registration_Year")
    private Integer registrationYear;

    @Column(nullable = false, name = "Power_Supply")
    private String powerSupply;

    @Column(nullable = false, name = "Price")
    private BigDecimal price;

    @Column(nullable = false, name = "Discount")
    private BigDecimal discount;

    @Column(nullable = false, name = "Accessories")
    private String accessories;

    @Column(nullable = false, name = "Is_New")
    private Boolean isNew;

    @Column(nullable = false, name = "Vehicle_Status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus vehicleStatus;

    @Column(nullable = false, name = "Vehicle_Type")
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", displacement=" + displacement +
                ", color='" + color + '\'' +
                ", power=" + power +
                ", transmission='" + transmission + '\'' +
                ", registrationYear=" + registrationYear +
                ", powerSupply='" + powerSupply + '\'' +
                ", price=" + price +
                ", discount=" + discount +
                ", accessories='" + accessories + '\'' +
                ", isNew=" + isNew +
                ", vehicleStatus=" + vehicleStatus +
                ", vehicleType=" + vehicleType +
                '}';
    }
}