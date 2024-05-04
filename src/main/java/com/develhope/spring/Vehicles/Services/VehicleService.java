package com.develhope.spring.Vehicles.Services;

import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleModel;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.Vehicles.Request.VehicleRequest;
import com.develhope.spring.Vehicles.Response.VehicleResponse;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    UserRepository userRepository;

    public Either<VehicleResponse, VehicleDTO> createVehicle(Long id, VehicleRequest vehicleRequest) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            if (userOptional.get().getUserType() == UserTypes.ADMIN) {
                VehicleModel vehicleModel = new VehicleModel(vehicleRequest.getBrand(), vehicleRequest.getModel(),
                        vehicleRequest.getDisplacement(), vehicleRequest.getColor(),
                        vehicleRequest.getPower(), vehicleRequest.getTransmission(), vehicleRequest.getRegistrationYear(),
                        vehicleRequest.getPowerSupply(), vehicleRequest.getPrice(), vehicleRequest.getDiscount(),
                        vehicleRequest.getAccessories(), vehicleRequest.getIsNew(), vehicleRequest.getVehicleStatus(),
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
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + userId + "not found"));
        }
        if (userOptional.get().getUserType() != UserTypes.BUYER && userOptional.get().getUserType() != UserTypes.SELLER) {
            return Either.left(new VehicleResponse(400, "only buyer and seller can get the vehicle"));
        }
        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(vehicleId);
        if (vehicleEntity.isEmpty()) {
            return Either.left(new VehicleResponse(404, "vehicle with id" + vehicleId + "not found"));
        }
        VehicleModel vehicleModel = VehicleModel.entityToModel(vehicleEntity.get());
        return Either.right(VehicleModel.modelToDTO(vehicleModel));
    }

    public Either<VehicleResponse, List<VehicleDTO>> getAllVehicle(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + userId + "not found"));
        }

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

    public Either<VehicleResponse, VehicleDTO> updateVehicle(Long userId, Long vehicleId, VehicleRequest request) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.get().getUserType() != UserTypes.ADMIN) {
            return Either.left(new VehicleResponse(403, "this user does not have the permission"));
        }
        Either<VehicleResponse, VehicleDTO> foundVehicle = getSingleVehicle(userId, vehicleId);
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
                request.getVehicleStatus(),
                request.getVehicleType()
        );
        VehicleEntity savedEntity = vehicleRepository.saveAndFlush(VehicleModel.modelToEntity(vehicleModel));
        VehicleModel myVehicleModel = VehicleModel.entityToModel(savedEntity);
        return Either.right(VehicleModel.modelToDTO(myVehicleModel));


    }


    public VehicleResponse deleteVehicle(Long userId, Long vehicleId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.get().getUserType() != UserTypes.ADMIN) {
            return new VehicleResponse(403, "this user does not have the permission");
        }
        Either<VehicleResponse, VehicleDTO> foundVehicle = getSingleVehicle(userId, vehicleId);
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

    public Either<VehicleResponse, List<VehicleDTO>> findByColor(Long userId, String color) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + userId + "not found"));
        }
        if (userOptional.get().getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "this user does not have the permission"));
        }
        List<VehicleEntity> vehicleEntities = vehicleRepository.findAll().stream().toList();
        List<VehicleEntity> myVehicleEntity = new ArrayList<>();
        for (VehicleEntity vehicleEntity : vehicleEntities) {
            if (Objects.equals(vehicleEntity.getColor(), color)) {
                myVehicleEntity.add(vehicleEntity);
            }
        }
        List<VehicleModel> vehicleModels = myVehicleEntity.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByModel(Long userId, String model) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + userId + "not found"));
        }
        if (userOptional.get().getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "this user does not have the permission"));
        }
        List<VehicleEntity> vehicleEntities = vehicleRepository.findAll().stream().toList();
        List<VehicleEntity> myVehicleEntity = new ArrayList<>();
        for (VehicleEntity vehicleEntity : vehicleEntities) {
            if (Objects.equals(vehicleEntity.getModel(), model)) {
                myVehicleEntity.add(vehicleEntity);
            }
        }
        List<VehicleModel> vehicleModels = myVehicleEntity.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByBrand(Long userId, String brand) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + userId + "not found"));
        }
        if (userOptional.get().getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "this user does not have the permission"));
        }
        List<VehicleEntity> vehicleEntities = vehicleRepository.findAll().stream().toList();
        List<VehicleEntity> myVehicleEntity = new ArrayList<>();
        for (VehicleEntity vehicleEntity : vehicleEntities) {
            if (Objects.equals(vehicleEntity.getModel(), brand)) {
                myVehicleEntity.add(vehicleEntity);
            }
        }
        List<VehicleModel> vehicleModels = myVehicleEntity.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByTransmission(Long userId, String transmission) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + userId + "not found"));
        }
        if (userOptional.get().getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "this user does not have the permission"));
        }
        List<VehicleEntity> vehicleEntities = vehicleRepository.findAll().stream().toList();
        List<VehicleEntity> myVehicleEntity = new ArrayList<>();
        for (VehicleEntity vehicleEntity : vehicleEntities) {
            if (Objects.equals(vehicleEntity.getModel(), transmission)) {
                myVehicleEntity.add(vehicleEntity);
            }
        }
        List<VehicleModel> vehicleModels = myVehicleEntity.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByPowerSupply(Long userId, String powerSupply) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "user with id" + userId + "not found"));
        }
        if (userOptional.get().getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "this user does not have the permission"));
        }
        List<VehicleEntity> vehicleEntities = vehicleRepository.findAll().stream().toList();
        List<VehicleEntity> myVehicleEntity = new ArrayList<>();
        for (VehicleEntity vehicleEntity : vehicleEntities) {
            if (Objects.equals(vehicleEntity.getModel(), powerSupply)) {
                myVehicleEntity.add(vehicleEntity);
            }
        }
        List<VehicleModel> vehicleModels = myVehicleEntity.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByAccessories(Long userId, List<String> accessories) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "User with ID " + userId + " not found"));
        }
        User user = userOptional.get();
        if (user.getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "This user does not have permission"));
        }
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByAccessoriesIn(accessories);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicles found with the specified accessories"));
        }
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByDisplacement(Long userId, Integer minDisplacement, Integer maxDisplacement) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "User with ID " + userId + " not found"));
        }
        User user = userOptional.get();
        if (user.getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "This user does not have permission"));
        }

        if (minDisplacement > maxDisplacement) {
            return Either.left(new VehicleResponse(400, "the minimum displacement cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByDisplacementBetween(minDisplacement, maxDisplacement);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicles found in the specified range"));
        }
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByPower(Long userId, Integer minPower, Integer maxPower) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "User with ID " + userId + " not found"));
        }
        User user = userOptional.get();
        if (user.getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "This user does not have permission"));
        }

        if (minPower > maxPower) {
            return Either.left(new VehicleResponse(400, "the minimum power cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByPowerBetween(minPower, maxPower);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicles found in the specified range"));
        }
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByRegistrationYear(Long userId, Integer minRegistrationYear, Integer maxRegistrationYear) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "User with ID " + userId + " not found"));
        }
        User user = userOptional.get();
        if (user.getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "This user does not have permission"));
        }

        if (minRegistrationYear > maxRegistrationYear) {
            return Either.left(new VehicleResponse(400, "the minimum registration year cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByRegistrationYearBetween(minRegistrationYear, maxRegistrationYear);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicles found in the specified range"));
        }
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByPrice(Long userId, BigDecimal minPrice, BigDecimal maxPrice) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "User with ID " + userId + " not found"));
        }
        User user = userOptional.get();
        if (user.getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "This user does not have permission"));
        }

        if (minPrice.compareTo(maxPrice) > 0) {
            return Either.left(new VehicleResponse(400, "the minimum price cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByPriceBetween(minPrice, maxPrice);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicles found in the specified range"));
        }
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByDiscount(Long userId, BigDecimal minPrice, BigDecimal maxPrice) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new VehicleResponse(404, "User with ID " + userId + " not found"));
        }
        User user = userOptional.get();
        if (user.getUserType() != UserTypes.BUYER) {
            return Either.left(new VehicleResponse(403, "This user does not have permission"));
        }

        if (minPrice.compareTo(maxPrice) > 0) {
            return Either.left(new VehicleResponse(400, "the minimum discount price cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByDiscountBetween(minPrice, maxPrice);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicles found in the specified range"));
        }
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    //TODO  isNew, vehicleStatus, vehicleType
}
