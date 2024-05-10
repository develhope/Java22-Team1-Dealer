package com.develhope.spring.Vehicles.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleModel;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.Vehicles.Request.VehicleRequest;
import com.develhope.spring.Vehicles.Response.VehicleResponse;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleCRUDService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    UserRepository userRepository;

    public Either<VehicleResponse, VehicleDTO> createVehicle(User user, VehicleRequest vehicleRequest) {
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isPresent()) {
            if (userOptional.get().getUserType() == UserTypes.ADMIN) {
                VehicleModel vehicleModel = new VehicleModel(vehicleRequest.getBrand(), vehicleRequest.getModel(),
                        vehicleRequest.getDisplacement(), vehicleRequest.getColor(),
                        vehicleRequest.getPower(), vehicleRequest.getTransmission(), vehicleRequest.getRegistrationYear(),
                        vehicleRequest.getPowerSupply(), vehicleRequest.getPrice(), vehicleRequest.getDiscount(),
                        vehicleRequest.getAccessories(), vehicleRequest.getIsNew(), VehicleStatus.convertFromString(vehicleRequest.getVehicleStatus()),
                        VehicleType.convertFromString(vehicleRequest.getVehicleType()));
                VehicleEntity vehicleEntity = VehicleModel.modelToEntity(vehicleModel);
                VehicleEntity result = vehicleRepository.save(vehicleEntity);
                VehicleModel resultModel = VehicleModel.entityToModel(result);
                return Either.right(VehicleModel.modelToDTO(resultModel));
            } else {
                return Either.left(new VehicleResponse(400, "only admin can create a vehicle"));
            }
        } else {
            return Either.left(new VehicleResponse(404, "user with id " + user.getId() + "not found"));
        }
    }

    public Either<VehicleResponse, VehicleDTO> getSingleVehicle(User user, Long vehicleId) {
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + user.getId() + "not found"));
        }
        if (userOptional.get().getUserType() != UserTypes.BUYER && userOptional.get().getUserType() != UserTypes.SELLER) {
            return Either.left(new VehicleResponse(400, "only buyer and seller can get the vehicle"));
        }
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(vehicleId);
        if (vehicleEntity.isEmpty()) {
            return Either.left(new VehicleResponse(404, "vehicle with id " + vehicleId + "not found"));
        }
        VehicleModel vehicleModel = VehicleModel.entityToModel(vehicleEntity.get());
        return Either.right(VehicleModel.modelToDTO(vehicleModel));
    }

    public Either<VehicleResponse, List<VehicleDTO>> getAllVehicle() {
        List<VehicleEntity> vehicleEntity = vehicleRepository.findAll();
        if (vehicleEntity.isEmpty()) {
            return Either.left(new VehicleResponse(404, "no cars list found"));
        }
        List<VehicleModel> vehicleModels = vehicleEntity.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, VehicleDTO> updateVehicle(User user, Long vehicleId, VehicleRequest request) {
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.get().getUserType() != UserTypes.ADMIN) {
            return Either.left(new VehicleResponse(403, "this user does not have the permission"));
        }
        Either<VehicleResponse, VehicleDTO> foundVehicle = getSingleVehicle(user, vehicleId);
        if (foundVehicle.isLeft()) {
            return foundVehicle;
        }
        VehicleModel vehicleModel = new VehicleModel(
                request.getBrand(),
                request.getModel(),
                request.getDisplacement(),
                request.getColor(),
                request.getPower(),
                request.getTransmission(),
                request.getRegistrationYear(),
                request.getPowerSupply(),
                request.getPrice(),
                request.getDiscount(),
                request.getAccessories(),
                request.getIsNew(),
                VehicleStatus.convertFromString(request.getVehicleStatus()),
                VehicleType.convertFromString(request.getVehicleType())
        );
        VehicleEntity savedEntity = vehicleRepository.saveAndFlush(VehicleModel.modelToEntity(vehicleModel));
        VehicleModel myVehicleModel = VehicleModel.entityToModel(savedEntity);
        return Either.right(VehicleModel.modelToDTO(myVehicleModel));


    }


    public VehicleResponse deleteVehicle(User user, Long vehicleId) {
        Optional<User> userOptional = userRepository.findById(user.getId());
        if (userOptional.get().getUserType() != UserTypes.ADMIN) {
            return new VehicleResponse(403, "this user does not have the permission");
        }
        Either<VehicleResponse, VehicleDTO> foundVehicle = getSingleVehicle(user, vehicleId);
        if (foundVehicle.isLeft()) {
            return foundVehicle.getLeft();
        }
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(vehicleId);

        try {
            vehicleRepository.delete(vehicleEntity.get());
            return new VehicleResponse(200, "Purchase deleted successfully");
        } catch (Exception e) {
            return new VehicleResponse(500, "Internal server error");
        }
    }

}
