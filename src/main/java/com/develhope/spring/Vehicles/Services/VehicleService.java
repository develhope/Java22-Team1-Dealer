package com.develhope.spring.Vehicles.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleModel;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    public VehicleDTO createVehicle(User user, VehicleDTO vehicleDTO) {
        if (user.getUserType() == UserTypes.ADMIN) {
            VehicleModel vehicleModel = VehicleModel.DTOtoModel(vehicleDTO);
            VehicleEntity vehicleEntity = VehicleModel.modelToEntity(vehicleModel);
            VehicleEntity result = vehicleRepository.save(vehicleEntity);
            VehicleModel resultModel = VehicleModel.entityToModel(result);
            return VehicleModel.modelToDTO(resultModel);
        } else {
            return null;
        }
    }
}
