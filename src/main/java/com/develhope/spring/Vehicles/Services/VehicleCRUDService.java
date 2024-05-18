package com.develhope.spring.Vehicles.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
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

    public Either<VehicleResponse, VehicleDTO> createVehicle(UserEntity userEntity, VehicleRequest vehicleRequest) {
        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
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
            return Either.left(new VehicleResponse(404, "user with id " + userEntity.getId() + "not found"));
        }
    }

    public Either<VehicleResponse, VehicleDTO> getSingleVehicle(UserEntity userEntity, Long vehicleId) {
        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + userEntity.getId() + "not found"));
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

    public Either<VehicleResponse, VehicleDTO> updateVehicle(UserEntity userEntity, Long vehicleId, VehicleRequest request) {
        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
        if (userOptional.get().getUserType() != UserTypes.ADMIN) {
            return Either.left(new VehicleResponse(403, "This user does not have the permission"));
        }
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(vehicleId);
        if (vehicleEntity.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicle found"));
        }

        vehicleEntity.get().setBrand(request.getBrand() == null ? vehicleEntity.get().getBrand() : request.getBrand());
        vehicleEntity.get().setModel(request.getModel() == null ? vehicleEntity.get().getModel() : request.getModel());
        vehicleEntity.get().setDisplacement(request.getDisplacement() == null ? vehicleEntity.get().getDisplacement() : request.getDisplacement());
        vehicleEntity.get().setColor(request.getColor() == null ? vehicleEntity.get().getColor() : request.getColor());
        vehicleEntity.get().setPower(request.getPower() == null ? vehicleEntity.get().getPower() : request.getPower());
        vehicleEntity.get().setTransmission(request.getTransmission() == null ? vehicleEntity.get().getTransmission() : request.getTransmission());
        vehicleEntity.get().setPowerSupply(request.getPowerSupply() == null ? vehicleEntity.get().getPowerSupply() : request.getPowerSupply());
        vehicleEntity.get().setPrice(request.getPrice() == null ? vehicleEntity.get().getPrice() : request.getPrice());
        vehicleEntity.get().setDiscount(request.getDiscount() == null ? vehicleEntity.get().getDiscount() : request.getDiscount());
        vehicleEntity.get().setAccessories(request.getAccessories() == null ? vehicleEntity.get().getAccessories() : request.getAccessories());
        vehicleEntity.get().setIsNew(request.getIsNew() == null ? vehicleEntity.get().getIsNew() : request.getIsNew());
        vehicleEntity.get().setVehicleStatus(request.getVehicleStatus() == null ? vehicleEntity.get().getVehicleStatus() : VehicleStatus.convertFromString(request.getVehicleStatus()));
        vehicleEntity.get().setVehicleType(request.getVehicleType() == null ? vehicleEntity.get().getVehicleType() : VehicleType.convertFromString(request.getVehicleType()));

        VehicleEntity savedEntity = vehicleRepository.saveAndFlush(vehicleEntity.get());
        VehicleModel myVehicleModel = VehicleModel.entityToModel(savedEntity);
        return Either.right(VehicleModel.modelToDTO(myVehicleModel));

    }


    public VehicleResponse deleteVehicle(UserEntity userEntity, Long vehicleId) {
        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
        if (userOptional.get().getUserType() != UserTypes.ADMIN) {
            return new VehicleResponse(403, "this user does not have the permission");
        }
        Either<VehicleResponse, VehicleDTO> foundVehicle = getSingleVehicle(userEntity, vehicleId);
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
