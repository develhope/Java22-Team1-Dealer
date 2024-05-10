package com.develhope.spring.Rent.Services;

import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.DTO.RentModel;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.Rent.Repositories.RentRepository;
import com.develhope.spring.Rent.Request.RentRequest;
import com.develhope.spring.Rent.Response.RentResponse;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;


    public Either<RentResponse, RentDTO> createRent(RentRequest rentRequest, Long userId, User userDetails) {

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new RentResponse(403, "User not found"));
        }
        User user = userOptional.get();

        if (user.getUserType() != UserTypes.BUYER && user.getUserType() != UserTypes.SELLER && user.getUserType() != UserTypes.ADMIN) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }

        Optional<VehicleEntity> vehicleEntityOptional = vehicleRepository.findById(rentRequest.getVehicleId());
        if (vehicleEntityOptional.isEmpty()) {
            return Either.left(new RentResponse(400, "Vehicle not found"));
        }
        VehicleEntity vehicleEntity = vehicleEntityOptional.get();
        if (vehicleEntity.getVehicleStatus() != VehicleStatus.RENTABLE) {
            return Either.left(new RentResponse(400, "Vehicle not available for rent"));
        }

        if (user.getUserType() == UserTypes.BUYER && !Objects.equals(user.getId(), userId)) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }

        BigDecimal dailyCost = new BigDecimal(rentRequest.getDailyCost().toString());
        long days = ChronoUnit.DAYS.between(rentRequest.getStartDate(), rentRequest.getEndDate());
        BigDecimal totalCost = dailyCost.multiply(BigDecimal.valueOf(days));

        RentModel rentModel = new RentModel(
                rentRequest.getStartDate(),
                rentRequest.getEndDate(),
                dailyCost,
                rentRequest.isPaid(),
                vehicleEntity,
                totalCost
        );

        RentEntity rentEntity = RentModel.modelToEntity(rentModel);
        if (user.getUserType() == UserTypes.SELLER || user.getUserType() == UserTypes.ADMIN) {
            rentEntity.setSoldBy(user);
        }

        RentEntity savedRentEntity = rentRepository.save(rentEntity);
        RentModel savedRentModel = RentModel.entityToModel(savedRentEntity);
        RentDTO savedRentDTO = RentModel.modelToDTO(savedRentModel);
        return Either.right(savedRentDTO);
    }


    public List<RentDTO> getRentList(User userDetails) {
        User user = userRepository.findByEmail(userDetails.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getUserType() == UserTypes.BUYER) {
            return rentRepository.findAllByUserId(user.getId()).stream().map(rentEntity -> {
                RentModel rentModel = RentModel.entityToModel(rentEntity);
                return RentModel.modelToDTO(rentModel);
            }).collect(Collectors.toList());
        } else if (user.getUserType() == UserTypes.SELLER) {
            return rentRepository.findAllActive().stream().map(rentEntity -> {
                RentModel rentModel = RentModel.entityToModel(rentEntity);
                return RentModel.modelToDTO(rentModel);
            }).collect(Collectors.toList());
        } else { // ADMIN
            return rentRepository.findAll().stream().map(rentEntity -> {
                RentModel rentModel = RentModel.entityToModel(rentEntity);
                return RentModel.modelToDTO(rentModel);
            }).collect(Collectors.toList());
        }
    }

    public RentDTO getRentById(Long id, User userDetails) {
        User user = userRepository.findByEmail(userDetails.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getUserType() == UserTypes.BUYER) {
            Optional<RentEntity> rentOptional = rentRepository.findByIdAndUserId(id, user.getId());
            if (rentOptional.isPresent()) {
                RentEntity rentEntity = rentOptional.get();
                RentModel rentModel = RentModel.entityToModel(rentEntity);
                return RentModel.modelToDTO(rentModel);
            } else {
                return null;
            }
        } else { // SELLER or ADMIN
            Optional<RentEntity> rentOptional = rentRepository.findById(id);
            if (rentOptional.isPresent()) {
                RentEntity rentEntity = rentOptional.get();
                RentModel rentModel = RentModel.entityToModel(rentEntity);
                return RentModel.modelToDTO(rentModel);
            } else {
                return null;
            }
        }
    }

    public Either<RentResponse, RentDTO> updateRentDates(Long id, RentRequest rentRequest, User userDetails) {
        User user = userRepository.findByEmail(userDetails.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        RentEntity rentEntity = rentRepository.findById(id).orElse(null);
        if (rentEntity == null) {
            return Either.left(new RentResponse(404, "Rent not found"));
        }
        if (user.getUserType() == UserTypes.BUYER && !Objects.equals(rentEntity.getUser().getId(), user.getId())) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
        // Update rental dates
        rentEntity.setStartDate(rentRequest.getStartDate());
        rentEntity.setEndDate(rentRequest.getEndDate());
        RentEntity updatedRentEntity = rentRepository.save(rentEntity);
        RentModel updatedRentModel = RentModel.entityToModel(updatedRentEntity);
        RentDTO updatedRentDTO = RentModel.modelToDTO(updatedRentModel);
        return Either.right(updatedRentDTO);
    }

    public Either<RentResponse, Void> deleteRent(Long id, User userDetails) {
        User user = userRepository.findByEmail(userDetails.getName()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        RentEntity rentEntity = rentRepository.findById(id).orElse(null);
        if (rentEntity == null) {
            return Either.left(new RentResponse(404, "Rent not found"));
        }
        if (user.getUserType() == UserTypes.BUYER && !Objects.equals(rentEntity.getUser().getId(), user.getId())) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
        rentRepository.delete(rentEntity);
        return Either.right(null);
    }

    public Either<RentResponse, String> payRent(Long id, Long userId) {
        RentEntity rentEntity = rentRepository.findById(id).orElse(null);
        if (rentEntity == null) {
            return Either.left(new RentResponse(404, "Rent not found"));
        }
        if (!Objects.equals(rentEntity.getUser().getId(), userId)) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
        rentEntity.setIsPaid(true);
        rentRepository.save(rentEntity);

        BigDecimal totalCost = rentEntity.getTotalCost();
        String paymentMessage = String.format("Payment successful. Total amount paid: %s", totalCost);
        return Either.right(paymentMessage);
    }
}