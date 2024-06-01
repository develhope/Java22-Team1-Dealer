package com.develhope.spring.rent.services;

import com.develhope.spring.rent.DTO.RentDTO;
import com.develhope.spring.rent.model.RentModel;
import com.develhope.spring.rent.entities.RentEntity;
import com.develhope.spring.rent.entities.RentLink;
import com.develhope.spring.rent.repositories.RentRepository;
import com.develhope.spring.rent.repositories.RentalsLinkRepository;
import com.develhope.spring.rent.request.RentRequest;
import com.develhope.spring.rent.response.RentResponse;
import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import com.develhope.spring.vehicles.services.VehicleCRUDService;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
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

    @Autowired
    private RentalsLinkRepository rentalsLinkRepository;
    @Autowired
    private VehicleCRUDService vehicleCRUDService;


    public Either<RentResponse, RentDTO> createRent(RentRequest rentRequest, Long userId, UserEntity userEntityDetails) {
        // Check if user type is NOT_DEFINED
        if (userEntityDetails.getUserType() == UserTypes.NOT_DEFINED) {
            return Either.left(new RentResponse(403, "User type is not defined"));
        }

        if (userEntityDetails.getUserType().equals(UserTypes.BUYER)) {
            if (userId == null) {
                userId = userEntityDetails.getId();
            } else {
                return Either.left(new RentResponse(403, "BUYER users can only create rents for themselves"));
            }
        } else if (!userEntityDetails.getUserType().equals(UserTypes.ADMIN) && !userEntityDetails.getUserType().equals(UserTypes.SELLER)) {
            return Either.left(new RentResponse(403, "Unauthorized user type"));
        }


        Either<RentResponse, UserEntity> userCheck = checkUserExists(userId);
        if (userCheck.isLeft()) {
            return Either.left(userCheck.getLeft());
        }
        UserEntity userEntity = userCheck.get();

        // Additional check for user not found
        if (userEntity == null) {
            return Either.left(new RentResponse(404, "User not found"));
        }

        Either<RentResponse, Void> authorizationCheck = checkUserAuthorization(userEntityDetails);
        if (authorizationCheck.isLeft()) {
            return Either.left(authorizationCheck.getLeft());
        }

        Optional<VehicleEntity> vehicleEntityOptional = vehicleRepository.findById(rentRequest.getVehicleId());
        if (vehicleEntityOptional.isEmpty()) {
            return Either.left(new RentResponse(400, "Vehicle not found"));
        }
        VehicleEntity vehicleEntity = vehicleEntityOptional.get();
        if (vehicleEntity.getVehicleStatus() != VehicleStatus.RENTABLE) {
            return Either.left(new RentResponse(400, "Vehicle not available for rent"));
        }

        if (rentRequest.getStartDate().isAfter(rentRequest.getEndDate())) {
            return Either.left(new RentResponse(400, "Start date must be before end date"));
        }

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
        vehicleEntity.setVehicleStatus(VehicleStatus.RENTED);
        vehicleRepository.save(vehicleEntity);


        RentEntity rentEntity = RentModel.modelToEntity(rentModel);
        RentEntity savedRentEntity = rentRepository.save(rentEntity);

        RentLink rentLink;
        if (userEntityDetails.getUserType() == UserTypes.SELLER) {
            rentLink = new RentLink(userEntity, savedRentEntity, userEntityDetails);
        } else {
            rentLink = new RentLink(userEntity, savedRentEntity);
        }
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
            return Either.left(new RentResponse(404, "Rent not found"));
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
        Either<RentResponse, Void> authorizationCheck = checkUserAuthorizationBuyerNotAuthorized(userEntityDetails);
        if (authorizationCheck.isLeft()) {
            return Either.left(authorizationCheck.getLeft());
        }

        Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentId(id);
        if (rentLinkOptional.isEmpty()) {
            return Either.left(new RentResponse(404, "Rent link not found"));
        }

        RentLink rentLink = rentLinkOptional.get();
        RentEntity rentEntity = rentLink.getRent();

        rentEntity.setActive(false);
        VehicleEntity vehicle = rentEntity.getVehicle();
        rentEntity.setVehicle(null);
        rentRepository.save(rentEntity);

        rentalsLinkRepository.delete(rentLink);

        if (vehicle != null) {
            vehicle.setVehicleStatus(VehicleStatus.RENTABLE);
            vehicleRepository.save(vehicle);
        }

        return Either.right(null);
    }

    public Either<RentResponse, String> payRent(Long id, Long userId, UserEntity userEntityDetails) {
        Optional<RentLink> rentLinkOptional = rentalsLinkRepository.findByRentId(id);
        if (rentLinkOptional.isEmpty()) {
            System.out.println("No RentLink found for ID: " + id); // Debug log
            return Either.left(new RentResponse(403, "Unauthorized user - Rent link not found"));
        }
        RentLink rentLink = rentLinkOptional.get();
        RentEntity rentEntity = rentLink.getRent();

        if (rentEntity.getIsPaid()) {
            return Either.left(new RentResponse(400, "Rent already paid"));
        }

        boolean isAuthorized = userEntityDetails.getUserType() == UserTypes.ADMIN ||
                userEntityDetails.getUserType() == UserTypes.SELLER ||
                (userEntityDetails.getUserType() == UserTypes.BUYER &&
                        rentLink.getBuyer() != null &&
                        userId.equals(rentLink.getBuyer().getId()));

        System.out.println("User Authorization: " + isAuthorized + " for User ID: " + userId + " with UserType: " + userEntityDetails.getUserType());
        if (!isAuthorized) {
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

        rentEntity.setActive(false);
        // Controllo se il veicolo è associato alla prenotazione
        if (rentEntity.getVehicle() != null) {
            // Imposto lo stato del veicolo come RENTABLE
            rentEntity.getVehicle().setVehicleStatus(VehicleStatus.RENTABLE);
            vehicleRepository.save(rentEntity.getVehicle());
        } else {
            return Either.left(new RentResponse(500, "Internal Server Error: No vehicle associated with the rent"));
        }
        rentEntity.setVehicle(null);
        rentRepository.save(rentEntity);

        return Either.right("Rent booking successfully set to inactive and vehicle status updated to RENTABLE.");
    }

    public Either<RentResponse, UserEntity> checkUserExists(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        return userOptional.<Either<RentResponse, UserEntity>>map(Either::right).orElseGet(() -> Either.left(new RentResponse(403, "User not found")));
    }

    public Either<RentResponse, Void> checkUserAuthorization(UserEntity userEntityDetails) {
        if (userEntityDetails.getUserType() != UserTypes.BUYER && userEntityDetails.getUserType() != UserTypes.SELLER && userEntityDetails.getUserType() != UserTypes.ADMIN) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
        return Either.right(null);
    }

    public Either<RentResponse, Void> checkUserAuthorizationBuyerNotAuthorized(UserEntity userEntityDetails) {
        if (userEntityDetails.getUserType() != UserTypes.SELLER && userEntityDetails.getUserType() != UserTypes.ADMIN) {
            return Either.left(new RentResponse(403, "Unauthorized user"));
        }
        return Either.right(null);
    }

    public Either<RentResponse, Boolean> checkRentIsActive(RentEntity rentEntity) {
        if (rentEntity.isActive()) {
            return Either.left(new RentResponse(400, "Rent is not active"));
        }
        return Either.right(true);
    }

    public void updateRentEntityDates(RentRequest rentRequest, RentEntity rentEntity) {
        rentEntity.setStartDate(rentRequest.getStartDate());
        rentEntity.setEndDate(rentRequest.getEndDate());
        rentEntity.setTotalCost(rentEntity.calculateTotalCost());

        if (!rentEntity.getIsPaid()) {
            rentEntity.setIsPaid(false);
        }
    }
}