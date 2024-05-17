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
        Either<RentResponse, UserEntity> userCheck = checkUserExists(userId);
        if (userCheck.isLeft()) {
            return Either.left(userCheck.getLeft());
        }
        UserEntity userEntity = userCheck.get();

        Either<RentResponse, Void> authorizationCheck = checkUserAuthorization(userEntityDetails);
        if (authorizationCheck.isLeft()) {
            return Either.left(authorizationCheck.getLeft());
        }

        Either<RentResponse, Void> buyerAuthorizationCheck = checkBuyerAuthorization(userEntityDetails, userId);
        if (buyerAuthorizationCheck.isLeft()) {
            return Either.left(buyerAuthorizationCheck.getLeft());
        }

        Optional<VehicleEntity> vehicleEntityOptional = vehicleRepository.findById(rentRequest.getVehicleId());
        if (vehicleEntityOptional.isEmpty()) {
            return Either.left(new RentResponse(400, "Vehicle not found"));
        }

        VehicleEntity vehicleEntity = vehicleEntityOptional.get();
        if (vehicleEntity.getVehicleStatus() != VehicleStatus.RENTABLE) {
            return Either.left(new RentResponse(400, "Vehicle not available for rent"));
        }

        vehicleEntity.setVehicleStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicleEntity);
        BigDecimal dailyCost = rentRequest.getDailyCost();
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
                    .map(rentLink -> RentModel.entityToModel(rentLink.getRent()))
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
            Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentId(id);
            if (rentLinkOptional.isPresent()) {
                RentLink rentLink = rentLinkOptional.get();
                if (rentLink.getBuyer().getId().equals(userEntityDetails.getId())) {
                    RentModel rentModel = RentModel.entityToModel(rentLink.getRent());
                    return RentModel.modelToDTO(rentModel);
                }
            }
        }
        return null;
    }

    public Either<RentResponse, RentDTO> updateRentDates(Long id, RentRequest rentRequest, UserEntity userEntityDetails) {
        //controllo della data, linizio non puo essere dopo la fine
        if (rentRequest.getStartDate().isAfter(rentRequest.getEndDate())) {
            return Either.left(new RentResponse(400, "Start date must be before end date"));
        }

        //trova il rentlink utilizzando il rentlinkid e lo userid
        Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentId(id);
        if (rentLinkOptional.isEmpty()) {
            return Either.left(new RentResponse(404, "Rent link not found"));
        }
        RentLink rentLink = rentLinkOptional.get();
        RentEntity rentEntity = rentLink.getRent();

        try {
            //controllo delle autorizzazioni user
            if (userEntityDetails.getUserType() == UserTypes.ADMIN || userEntityDetails.getUserType() == UserTypes.SELLER) {
                updateRentEntityDates(rentRequest, rentEntity);
            } else if (userEntityDetails.getUserType() == UserTypes.BUYER && rentLink.getBuyer().getId().equals(userEntityDetails.getId()) && rentEntity.isActive()) {
                updateRentEntityDates(rentRequest, rentEntity);
            } else {
                return Either.left(new RentResponse(403, "Unauthorized user or rent is not active"));
            }

            //salvataggio del rent nel repository e nel linkrepository
            RentEntity updatedRentEntity = rentRepository.save(rentEntity);
            rentLink.setRent(updatedRentEntity);
            rentalsLinkRepository.save(rentLink);

            RentModel updatedRentModel = RentModel.entityToModel(updatedRentEntity);
            RentDTO updatedRentDTO = RentModel.modelToDTO(updatedRentModel);
            return Either.right(updatedRentDTO);
        } catch (Exception e) {
            return Either.left(new RentResponse(500, "Error updating rent: " + e.getMessage()));
        }
    }

    public Either<RentResponse, Void> deleteRent(Long id, UserEntity userEntityDetails) {
        Either<RentResponse, Void> authorizationCheck = checkUserAuthorization(userEntityDetails);
        if (authorizationCheck.isLeft()) {
            return Either.left(authorizationCheck.getLeft());
        }

        Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentId(id);
        if (rentLinkOptional.isEmpty()) {
            return Either.left(new RentResponse(404, "Rent link not found"));
        }

        RentLink rentLink = rentLinkOptional.get();
        RentEntity rentEntity = rentLink.getRent();
        Either<RentResponse, Boolean> activeCheck = checkRentIsActive(rentEntity);
        if (activeCheck.isLeft()) {
            return Either.left(new RentResponse(400, "Rent is not Active"));
        }

        rentRepository.delete(rentEntity);

        VehicleEntity vehicleEntity = rentEntity.getVehicleId();
        if (vehicleEntity != null) {
            vehicleEntity.setVehicleStatus(VehicleStatus.RENTABLE);
            vehicleRepository.save(vehicleEntity);
        }

        return Either.right(null);
    }

    public Either<RentResponse, String> payRent(Long id, Long userId, UserEntity userEntityDetails) {
        Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentId(id);
        if (rentLinkOptional.isEmpty()) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
        RentLink rentLink = rentLinkOptional.get();
        RentEntity rentEntity = rentLink.getRent();

        if (rentEntity.getIsPaid()) {
            return Either.left(new RentResponse(400, "Rent already paid"));
        }

        if (userEntityDetails.getUserType() == UserTypes.BUYER && !userId.equals(rentLink.getBuyer().getId())) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }

        rentEntity.setIsPaid(true);
        rentEntity.setActive(true);
        rentRepository.save(rentEntity);

        BigDecimal totalCost = rentEntity.getTotalCost();
        String paymentMessage = "Payment successful. Total amount paid: " + totalCost;

        if (userEntityDetails.getUserType() == UserTypes.BUYER) {
            paymentMessage += ", enjoy your ride!";
        }

        return Either.right(paymentMessage);
    }

    public Either<RentResponse, String> deleteBooking(Long rentId, UserEntity userEntityDetails) {
        Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentId(rentId);
        if (rentLinkOptional.isEmpty()) {
            return Either.left(new RentResponse(404, "Rent not found or does not belong to the user"));
        }

        RentLink rentLink = rentLinkOptional.get();
        RentEntity rentEntity = rentLink.getRent();

        Either<RentResponse, Boolean> activeCheck = checkRentIsActive(rentEntity);
        if (activeCheck.isRight()) {
            return Either.left(new RentResponse(400, "Rent is Active and cannot be deleted"));
        }

        VehicleEntity vehicleEntity = rentEntity.getVehicleId();
        if (vehicleEntity != null) {
            vehicleEntity.setVehicleStatus(VehicleStatus.RENTABLE);
            vehicleRepository.save(vehicleEntity);
        }

        rentRepository.delete(rentEntity);
        return Either.right("Rent booking successfully deleted.");
    }

    private Either<RentResponse, UserEntity> checkUserExists(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        return userOptional.<Either<RentResponse, UserEntity>>map(Either::right).orElseGet(() -> Either.left(new RentResponse(403, "User not found")));
    }

    private Either<RentResponse, Void> checkUserAuthorization(UserEntity userEntityDetails) {
        if (userEntityDetails.getUserType() != UserTypes.BUYER && userEntityDetails.getUserType() != UserTypes.SELLER && userEntityDetails.getUserType() != UserTypes.ADMIN) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
        return Either.right(null);
    }

    private Either<RentResponse, Void> checkBuyerAuthorization(UserEntity userEntityDetails, Long userId) {
        if (userEntityDetails.getUserType() == UserTypes.BUYER && !userEntityDetails.getId().equals(userId)) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
        return Either.right(null);
    }

    private Either<RentResponse, Boolean> checkRentIsActive(RentEntity rentEntity) {
        if (rentEntity.isActive()) {
            return Either.left(new RentResponse(400, "Rent is not active"));
        }
        return Either.right(true);
    }

    private void updateRentEntityDates(RentRequest rentRequest, RentEntity rentEntity) {
        rentEntity.setStartDate(rentRequest.getStartDate());
        rentEntity.setEndDate(rentRequest.getEndDate());
        rentEntity.setTotalCost(rentEntity.calculateTotalCost());

        if (!rentEntity.getIsPaid()) {
            rentEntity.setIsPaid(false);
        }
    }
}