package com.develhope.spring.Vehicles.Services;

import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleModel;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.Vehicles.Response.VehicleResponse;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class VehicleResearchService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    public Either<VehicleResponse, List<VehicleDTO>> findByColor(String color) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByModel(String model) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByBrand(String brand) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByTransmission(String transmission) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByPowerSupply(String powerSupply) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByAccessories(List<String> accessories) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByDisplacement(Integer minDisplacement, Integer maxDisplacement) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByPower(Integer minPower, Integer maxPower) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByRegistrationYear(Integer minRegistrationYear, Integer maxRegistrationYear) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByDiscount(BigDecimal minPrice, BigDecimal maxPrice) {
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

    public Either<VehicleResponse, List<VehicleDTO>> findByIsNew(boolean isNew) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByIsNew(isNew);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicles found"));
        }
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByVehicleStatus(VehicleStatus vehicleStatus) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByVehicleStatus(vehicleStatus);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicles found"));
        }
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleResponse, List<VehicleDTO>> findByVehicleType(VehicleType vehicleType) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByVehicleType(vehicleType);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleResponse(404, "No vehicles found"));
        }
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

}
