package com.develhope.spring.Vehicles.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class VehicleEntity {

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
    private List<String> accessories;

    @Column(nullable = false, name = "Is_New")
    private Boolean isNew;

    @Column(nullable = false, name = "Vehicle_Status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus vehicleStatus;

    @Column(nullable = false, name = "Vehicle_Type")
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

}
