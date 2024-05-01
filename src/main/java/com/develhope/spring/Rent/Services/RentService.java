package com.develhope.spring.Rent.Services;

import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.DTO.RentModel;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.Rent.Repositories.RentRepository;
import com.develhope.spring.Rent.Request.RentRequest;
import com.develhope.spring.Rent.Response.RentResponse;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.User.Repositories.UserRepository;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public Either<RentResponse, RentDTO> createRent(Long userId, Long receiverId, RentRequest rentRequest) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Either.left(new RentResponse(400, "User not found"));
        }

        if (UserTypes.BUYER.equals(user.getUserType()) && userId.equals(receiverId)) {
            receiverId = userId;
        } else {
            if (receiverId == null) {
                return Either.left(new RentResponse(400, "ReceiverId is required"));
            }
            User receiver = userRepository.findById(receiverId).orElse(null);
            if (receiver == null) {
                return Either.left(new RentResponse(400, "Receiver not found"));
            }
        }

        if (!userId.equals(receiverId)) {
            return Either.left(new RentResponse(403, "Access denied"));
        }

        RentModel rentModel = new RentModel(rentRequest.getStartDate(), rentRequest.getEndDate(), rentRequest.getDailyCost(), rentRequest.isPaid(), rentRequest.getVehicleId());
        RentEntity rentEntity = RentModel.modelToEntity(rentModel);

        VehicleEntity vehicleEntity = vehicleRepository.findById(rentEntity.getVehicleId()).orElse(null);
        if (vehicleEntity == null) {
            return Either.left(new RentResponse(404, "Vehicle not found"));
        }

        RentModel savedRent = RentModel.entityToModel(rentRepository.save(rentEntity));
        RentDTO savedRentDTO = RentModel.modelToDTO(savedRent);
        return Either.right(savedRentDTO);
    }

    public Either<RentResponse, List<RentDTO>> getRentsByUserId(Long userId) {
        List<RentEntity> rents = rentRepository.findByUserId(userId);
        if (rents.isEmpty())
            return Either.left(new RentResponse(404, "No rents found for this user"));

        List<RentDTO> rentDTOs = rents.stream()
                .map(rentEntity -> {
                    RentModel rentModel = RentModel.entityToModel(rentEntity);
                    return RentModel.modelToDTO(rentModel);
                })
                .collect(Collectors.toList());
        return Either.right(rentDTOs);
    }

    public Either<RentResponse, RentDTO> getRentById(Long userId, Long rentId) {
        RentEntity rentEntity = rentRepository.findById(rentId).orElse(null);
        if (rentEntity == null || !rentEntity.getUser().getId().equals(userId))
            return Either.left(new RentResponse(404, "Rent not found"));

        RentModel rentModel = RentModel.entityToModel(rentEntity);
        RentDTO rentDTO = RentModel.modelToDTO(rentModel);
        return Either.right(rentDTO);
    }

    public Either<RentResponse, RentDTO> updateRentDates(Long userId, Long rentId, RentRequest rentRequest) {
        RentEntity rentEntity = rentRepository.findById(rentId).orElse(null);
        if (rentEntity == null || !rentEntity.getUser().getId().equals(userId))
            return Either.left(new RentResponse(404, "Rent not found"));

        rentEntity.setStartDate(rentRequest.getStartDate());
        rentEntity.setEndDate(rentRequest.getEndDate());
        rentEntity.setTotalCost(rentEntity.calculateTotalCost());

        RentEntity updatedRent = rentRepository.save(rentEntity);
        RentModel updatedRentModel = RentModel.entityToModel(updatedRent);
        RentDTO updatedRentDTO = RentModel.modelToDTO(updatedRentModel);
        return Either.right(updatedRentDTO);
    }

    public Either<RentResponse, Boolean> deleteRent(Long userId, Long rentId) {
        RentEntity rentEntity = rentRepository.findById(rentId).orElse(null);
        if (rentEntity == null || !rentEntity.getUser().getId().equals(userId))
            return Either.left(new RentResponse(404, "Rent not found"));

        rentRepository.delete(rentEntity);
        return Either.right(true);
    }
}