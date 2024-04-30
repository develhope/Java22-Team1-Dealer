package com.develhope.spring.Vehicles.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.DTO.VehicleModel;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.Vehicles.Request.VehicleRequest;
import com.develhope.spring.Vehicles.Response.VehicleResponse;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    UserRepository userRepository;

    public Either<VehicleResponse, VehicleDTO> createVehicle(Long id, VehicleRequest vehicleRequest) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            if (user.get().getUserType() == UserTypes.ADMIN) {
                VehicleModel vehicleModel = new VehicleModel(vehicleRequest.getBrand(), vehicleRequest.getModel(),
                        vehicleRequest.getDisplacement(), vehicleRequest.getColor(),
                        vehicleRequest.getPower(), vehicleRequest.getTransmission(), vehicleRequest.getRegistrationYear(), vehicleRequest.getPowerSupply(),
                        vehicleRequest.getPrice(), vehicleRequest.getDiscount(), vehicleRequest.getAccessories(), vehicleRequest.getIsNew(), vehicleRequest.getVehicleStatus(),
                        vehicleRequest.getVehicleType());
                VehicleEntity vehicleEntity = VehicleModel.modelToEntity(vehicleModel);
                VehicleEntity result = vehicleRepository.save(vehicleEntity);
                VehicleModel resultModel = VehicleModel.entityToModel(result);
                return Either.right(VehicleModel.modelToDTO(resultModel));
            } else {
                return Either.left(new VehicleResponse(400, "only admin can create a vehicle"));
            }
        } else {
            return Either.left(new VehicleResponse(404, "user with id" + id + "not found"));
        }
    }

    public Either<VehicleResponse, VehicleDTO> getSingleVehicle(Long userId, Long vehicleId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + userId + "not found"));
        }
        if (user.get().getUserType() != UserTypes.BUYER && user.get().getUserType() != UserTypes.SELLER) {
            return Either.left(new VehicleResponse(400, "only buyer and seller can get the vehicle"));
        }
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(vehicleId);
        if (vehicleEntity.isEmpty()) {
            return Either.left(new VehicleResponse(404, "vehicle with id" + vehicleId + "not found"));
        }
        VehicleModel vehicleModel = VehicleModel.entityToModel(vehicleEntity.get());
        return Either.right(VehicleModel.modelToDTO(vehicleModel));
    }
}
