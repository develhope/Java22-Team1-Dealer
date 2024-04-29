package com.develhope.spring.Vehicles.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.Vehicles.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    public VehicleDTO createVehicle(User user, VehicleDTO vehicleDTO) {
        if (user.getUserType() == UserTypes.ADMIN) {
            VehicleEntity vehicle = vehicleDTO.toModel().toEntity();
            return vehicleRepository.save(vehicle).toModel().toDTO();
        } else {
            return null;
        }
    }
}
