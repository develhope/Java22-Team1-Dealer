package com.develhope.spring.vehicles.services;

import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.vehicles.DTO.VehicleDTO;
import com.develhope.spring.vehicles.model.VehicleModel;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.entities.VehicleType;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import com.develhope.spring.vehicles.request.VehicleRequest;
import com.develhope.spring.vehicles.response.VehicleErrorResponse;
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

    public Either<VehicleErrorResponse, VehicleDTO> createVehicle(UserEntity userEntity, VehicleRequest vehicleRequest) {
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
                return Either.left(new VehicleErrorResponse(400, "only admin can create a vehicle"));
            }
        } else {
            return Either.left(new VehicleErrorResponse(404, "user with id " + userEntity.getId() + "not found"));
        }
    }

    public Either<VehicleErrorResponse, VehicleDTO> getSingleVehicle(UserEntity userEntity, Long vehicleId) {
        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "user with id" + userEntity.getId() + "not found"));
        }
        if (userOptional.get().getUserType() != UserTypes.BUYER && userOptional.get().getUserType() != UserTypes.SELLER) {
            return Either.left(new VehicleErrorResponse(400, "only buyer and seller can get the vehicle"));
        }
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(vehicleId);
        if (vehicleEntity.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "vehicle with id " + vehicleId + "not found"));
        }
        VehicleModel vehicleModel = VehicleModel.entityToModel(vehicleEntity.get());
        return Either.right(VehicleModel.modelToDTO(vehicleModel));
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> getAllVehicle() {
        List<VehicleEntity> vehicleEntity = vehicleRepository.findAll();
        if (vehicleEntity.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "no cars list found"));
        }
        List<VehicleModel> vehicleModels = vehicleEntity.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, VehicleDTO> updateVehicle(UserEntity userEntity, Long vehicleId, VehicleRequest request) {
        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
        if (userOptional.get().getUserType() != UserTypes.ADMIN) {
            return Either.left(new VehicleErrorResponse(403, "This user does not have the permission"));
        }
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(vehicleId);
        if (vehicleEntity.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicle found"));
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


    public VehicleErrorResponse deleteVehicle(UserEntity userEntity, Long vehicleId) {
        Optional<UserEntity> userOptional = userRepository.findById(userEntity.getId());
        if (userOptional.get().getUserType() != UserTypes.ADMIN) {
            return new VehicleErrorResponse(403, "this user does not have the permission");
        }
        Either<VehicleErrorResponse, VehicleDTO> foundVehicle = getSingleVehicle(userEntity, vehicleId);
        if (foundVehicle.isLeft()) {
            return foundVehicle.getLeft();
        }
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(vehicleId);

        try {
            vehicleRepository.delete(vehicleEntity.get());
            return new VehicleErrorResponse(200, "Purchase deleted successfully");
        } catch (Exception e) {
            return new VehicleErrorResponse(500, "Internal server error");
        }
    }

}
