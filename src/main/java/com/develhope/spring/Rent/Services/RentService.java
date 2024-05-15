package com.develhope.spring.Rent.Services;

import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.DTO.RentModel;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.Rent.Entities.RentLink;
import com.develhope.spring.Rent.Repositories.RentRepository;
import com.develhope.spring.Rent.Repositories.RentalsLinkRepository;
import com.develhope.spring.Rent.Request.RentRequest;
import com.develhope.spring.Rent.Response.RentResponse;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
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
import java.util.Collections;

@Service
public class RentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RentRepository rentRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RentalsLinkRepository rentalsLinkRepository;

    public Either<RentResponse, RentDTO> createRent(RentRequest rentRequest, Long userId, UserEntity userEntityDetails) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new RentResponse(403, "User not found"));
        }
        UserEntity userEntity = userOptional.get();

        if (userEntityDetails.getUserType() != UserTypes.BUYER && userEntityDetails.getUserType() != UserTypes.SELLER && userEntityDetails.getUserType() != UserTypes.ADMIN) {
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

        if (userEntityDetails.getUserType() == UserTypes.BUYER && !Objects.equals(userEntityDetails.getId(), userId)) {
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
        rentEntity.setUserEntity(userEntity);

        RentEntity savedRentEntity = rentRepository.save(rentEntity);
        RentLink rentLink = new RentLink(userEntity, savedRentEntity);
        RentLink savedRentLink = rentalsLinkRepository.save(rentLink);

        RentModel savedRentModel = RentModel.entityToModel(savedRentEntity);
        RentDTO savedRentDTO = RentModel.modelToDTO(savedRentModel);
        return Either.right(savedRentDTO);
    }

    public List<RentDTO> getRentList(UserEntity userEntityDetails) {
        if (userEntityDetails.getUserType() == UserTypes.ADMIN || userEntityDetails.getUserType() == UserTypes.SELLER) {
            return rentRepository.findAll().stream()
                    .map(RentModel::entityToModel)
                    .map(RentModel::modelToDTO)
                    .collect(Collectors.toList());
        } else if (userEntityDetails.getUserType() == UserTypes.BUYER) {
            return rentalsLinkRepository.findAllByBuyer_Id(userEntityDetails.getId()).stream()
                    .map(rentLink -> RentModel.entityToModel(rentLink.getRentEntity()))
                    .map(RentModel::modelToDTO)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    public RentDTO getRentById(Long id, UserEntity userEntityDetails) {
        if (userEntityDetails.getUserType() == UserTypes.ADMIN || userEntityDetails.getUserType() == UserTypes.SELLER) {
            Optional<RentEntity> rentEntityOptional = rentRepository.findById(id);
            if (rentEntityOptional.isPresent()) {
                RentModel rentModel = RentModel.entityToModel(rentEntityOptional.get());
                return RentModel.modelToDTO(rentModel);
            }
        } else if (userEntityDetails.getUserType() == UserTypes.BUYER) {
            Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentIdAndBuyerId(id, userEntityDetails.getId());
            if (rentLinkOptional.isPresent()) {
                RentLink rentLink = rentLinkOptional.get();
                if (rentLink.getBuyer().getId().equals(userEntityDetails.getId())) {
                    RentModel rentModel = RentModel.entityToModel(rentLink.getRentEntity());
                    return RentModel.modelToDTO(rentModel);
                }
            }
        }
        return null;
    }

    public Either<RentResponse, RentDTO> updateRentDates(Long id, RentRequest rentRequest, UserEntity userEntityDetails) {
        if (userEntityDetails.getUserType() == UserTypes.ADMIN || userEntityDetails.getUserType() == UserTypes.SELLER) {
            Optional<RentEntity> rentEntityOptional = rentRepository.findById(id);
            if (rentEntityOptional.isEmpty() || !rentEntityOptional.get().isActive()) {
                return Either.left(new RentResponse(404, "Rent not found or not active"));
            }
            RentEntity rentEntity = rentEntityOptional.get();
            rentEntity.setStartDate(rentRequest.getStartDate());
            rentEntity.setEndDate(rentRequest.getEndDate());
            RentEntity updatedRentEntity = rentRepository.save(rentEntity);
            RentModel updatedRentModel = RentModel.entityToModel(updatedRentEntity);
            RentDTO updatedRentDTO = RentModel.modelToDTO(updatedRentModel);
            return Either.right(updatedRentDTO);
        } else if (userEntityDetails.getUserType() == UserTypes.BUYER) {
            Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentIdAndBuyerId(id, userEntityDetails.getId());
            if (rentLinkOptional.isEmpty() || !rentLinkOptional.get().getRentEntity().isActive()) {
                return Either.left(new RentResponse(403, "Unauthorized or inactive rent"));
            }
            RentLink rentLink = rentLinkOptional.get();
            RentEntity rentEntity = rentLink.getRentEntity();
            rentEntity.setStartDate(rentRequest.getStartDate());
            rentEntity.setEndDate(rentRequest.getEndDate());
            RentEntity updatedRentEntity = rentRepository.save(rentEntity);
            RentModel updatedRentModel = RentModel.entityToModel(updatedRentEntity);
            RentDTO updatedRentDTO = RentModel.modelToDTO(updatedRentModel);
            return Either.right(updatedRentDTO);
        } else {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
    }

    public Either<RentResponse, Void> deleteRent(Long id, UserEntity userEntityDetails) {
        if (userEntityDetails.getUserType() != UserTypes.SELLER && userEntityDetails.getUserType() != UserTypes.ADMIN) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }

        Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentIdAndBuyerId(id, userEntityDetails.getId());
        if (rentLinkOptional.isEmpty()) {
            return Either.left(new RentResponse(404, "Rent link not found"));
        }

        RentLink rentLink = rentLinkOptional.get();
        RentEntity rentEntity = rentLink.getRentEntity();
        if (!rentEntity.isActive()) {
            return Either.left(new RentResponse(400, "Rent is not active"));
        }

        rentRepository.delete(rentEntity);
        return Either.right(null);
    }

    public Either<RentResponse, String> payRent(Long id, Long userId, UserEntity userEntityDetails) {
        Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentIdAndBuyerId(id, userId);
        if (rentLinkOptional.isEmpty()) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
        RentLink rentLink = rentLinkOptional.get();
        RentEntity rentEntity = rentLink.getRentEntity();

        if (rentEntity.getIsPaid()) {
            return Either.left(new RentResponse(400, "Rent already paid"));
        }

        if (userEntityDetails.getUserType() == UserTypes.BUYER && !userId.equals(rentLink.getBuyer().getId())) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }

        rentEntity.setIsPaid(true);
        rentRepository.save(rentEntity);

        BigDecimal totalCost = rentEntity.getTotalCost();
        String paymentMessage = "Payment successful. Total amount paid: " + totalCost;

        if (userEntityDetails.getUserType() == UserTypes.BUYER) {
            paymentMessage += ", enjoy your ride!";
        }

        return Either.right(paymentMessage);
    }

    public Either<RentResponse, String> deleteBooking(Long rentId, UserEntity userEntityDetails) {
        Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentIdAndBuyerId(rentId, userEntityDetails.getId());
        if (rentLinkOptional.isEmpty()) {
            return Either.left(new RentResponse(404, "Rent not found or does not belong to the user"));
        }

        RentLink rentLink = rentLinkOptional.get();
        RentEntity rentEntity = rentLink.getRentEntity();

        if (rentEntity.getIsPaid()) {
            return Either.left(new RentResponse(400, "Rent has already been paid and cannot be deleted"));
        }

        rentRepository.delete(rentEntity);
        return Either.right("Rent booking successfully deleted.");
    }
}