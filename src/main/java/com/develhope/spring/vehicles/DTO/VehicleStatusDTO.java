package com.develhope.spring.vehicles.DTO;

import com.develhope.spring.vehicles.entities.VehicleStatus;

public class VehicleStatusDTO {

    private VehicleStatus vehicleStatus;

    public VehicleStatusDTO(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public VehicleStatusDTO() {

    }

    public VehicleStatus getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(VehicleStatus vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    @Override
    public String toString() {
        return "VehicleStatusDTO{" +
                "vehicleStatus=" + vehicleStatus +
                '}';
    }
}
