package com.develhope.spring.Vehicles.DTO;

import com.develhope.spring.Vehicles.Entities.VehicleStatus;

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
