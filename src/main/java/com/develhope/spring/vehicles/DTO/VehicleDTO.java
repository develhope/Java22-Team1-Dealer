package com.develhope.spring.vehicles.DTO;

import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.entities.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDTO {

    private Long vehicleId;
    private String brand;
    private String model;
    private Integer displacement;
    private String color;
    private Integer power;
    private String transmission;
    private Integer registrationYear;
    private String powerSupply;
    private BigDecimal price;
    private BigDecimal discount;
    private List<String> accessories;
    private Boolean isNew;
    private VehicleStatus vehicleStatus;
    private VehicleType vehicleType;

    @Override
    public String toString() {
        return "VehicleDTO{" +
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
                ", accessories=" + accessories +
                ", isNew=" + isNew +
                ", vehicleStatus=" + vehicleStatus +
                ", vehicleType=" + vehicleType +
                '}';
    }
}
