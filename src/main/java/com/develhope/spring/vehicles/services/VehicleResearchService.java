package com.develhope.spring.vehicles.services;

import com.develhope.spring.dealershipstatistics.service.DealershipStatisticsService;
import com.develhope.spring.vehicles.DTO.VehicleDTO;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.entities.VehicleType;
import com.develhope.spring.vehicles.model.VehicleModel;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import com.develhope.spring.vehicles.response.VehicleErrorResponse;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleResearchService {
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    DealershipStatisticsService dealershipStatisticsService;

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByColor(String color) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByColor(color);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicle Found"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleModel> vehicleModels = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByModel(String model) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByModel(model);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicle Found"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleModel> vehicleModels = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByBrand(String brand) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByBrand(brand);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicle Found"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleModel> vehicleModels = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByTransmission(String transmission) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByTransmission(transmission);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicle Found"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleModel> vehicleModels = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByPowerSupply(String powerSupply) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByPowerSupply(powerSupply);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicle Found"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleModel> vehicleModels = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .toList();

        List<VehicleDTO> vehicleDTOs = vehicleModels.stream()
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByAccessories(List<String> accessories) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByAccessoriesIn(accessories);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicles found with the specified accessories"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByDisplacement(Integer minDisplacement, Integer maxDisplacement) {
        if (minDisplacement > maxDisplacement) {
            return Either.left(new VehicleErrorResponse(400, "the minimum displacement cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByDisplacementBetween(minDisplacement, maxDisplacement);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicles found in the specified range"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByPower(Integer minPower, Integer maxPower) {
        if (minPower > maxPower) {
            return Either.left(new VehicleErrorResponse(400, "the minimum power cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByPowerBetween(minPower, maxPower);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicles found in the specified range"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByRegistrationYear(Integer minRegistrationYear, Integer maxRegistrationYear) {
        if (minRegistrationYear > maxRegistrationYear) {
            return Either.left(new VehicleErrorResponse(400, "the minimum registration year cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByRegistrationYearBetween(minRegistrationYear, maxRegistrationYear);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicles found in the specified range"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice.compareTo(maxPrice) > 0) {
            return Either.left(new VehicleErrorResponse(400, "the minimum price cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByPriceBetween(minPrice, maxPrice);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicles found in the specified range"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByDiscount(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice.compareTo(maxPrice) > 0) {
            return Either.left(new VehicleErrorResponse(400, "the minimum discount price cannot be higher than the maximum"));
        }

        List<VehicleEntity> vehicleEntities = vehicleRepository.findByDiscountBetween(minPrice, maxPrice);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicles found in the specified range"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByIsNew(boolean isNew) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByIsNew(isNew);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicles found"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByVehicleStatus(VehicleStatus vehicleStatus) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByVehicleStatus(vehicleStatus);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicles found"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

    public Either<VehicleErrorResponse, List<VehicleDTO>> findByVehicleType(VehicleType vehicleType) {
        List<VehicleEntity> vehicleEntities = vehicleRepository.findByVehicleType(vehicleType);
        if (vehicleEntities.isEmpty()) {
            return Either.left(new VehicleErrorResponse(404, "No vehicles found"));
        }
        vehicleEntities.forEach(vehicle -> dealershipStatisticsService.updateSearchStatistics(vehicle.getBrand(), vehicle.getModel()));
        List<VehicleDTO> vehicleDTOs = vehicleEntities.stream()
                .map(VehicleModel::entityToModel)
                .map(VehicleModel::modelToDTO)
                .collect(Collectors.toList());

        return Either.right(vehicleDTOs);
    }

}
