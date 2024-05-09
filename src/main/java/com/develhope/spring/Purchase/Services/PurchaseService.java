package com.develhope.spring.Purchase.Services;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.DTO.PurchaseModel;
import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import com.develhope.spring.Purchase.Request.PurchaseRequest;
import com.develhope.spring.Purchase.Response.PurchaseResponse;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.order.Entities.OrderEntity;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    public Either<PurchaseResponse, PurchaseDTO> createPurchase(User user, PurchaseRequest purchaseRequest) {
        //check if user has the necessary role
        if (user.getUserType() != UserTypes.BUYER && user.getUserType() != UserTypes.ADMIN) {
            return Either.left(new PurchaseResponse(403, "This user does not have permission to create a purchase"));
        }

        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(purchaseRequest.getVehicleId());
        if(vehicleEntity.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "No vehicle found with such Id"));
        }

        //check if deposit is not negative
        if (purchaseRequest.getDeposit() < 0) {
            return Either.left(new PurchaseResponse(400, "Deposit cannot be negative"));
        }

        PurchaseModel purchaseModel = new PurchaseModel(purchaseRequest.getDeposit(), purchaseRequest.isPaid(),
                purchaseRequest.getStatus(), vehicleEntity.get());
        PurchaseEntity result = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));

        PurchaseModel resultModel = PurchaseModel.entityToModel(result);
        return Either.right(PurchaseModel.modelToDto(resultModel));
    }

    public Either<PurchaseResponse, PurchaseDTO> getSinglePurchase(User user, Long purchadeId) {
        Optional<User> userOptional = userRepository.findById(user.getId());
        Optional<PurchaseEntity> purchaseEntity = userOptional.get().getPurchaseEntities().stream().filter(pe -> pe.getPurchaseId().equals(purchadeId)).findFirst();
        if(purchaseEntity.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "Purchase not found"));
        }

        PurchaseModel purchaseModel = PurchaseModel.entityToModel(purchaseEntity.get());
        return Either.right(PurchaseModel.modelToDto(purchaseModel));
    }

    public Either<PurchaseResponse, List<PurchaseDTO>> getAllPurchases(User user) {
        //checks if user exists
        Optional<User> userOptional = userRepository.findById(user.getId());
        List<PurchaseEntity> userPurchase = userOptional.get().getPurchaseEntities();
        if(userPurchase.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "Purchases not found"));
        }

        return Either.right(userPurchase.stream()
                .map(
                        PurchaseModel::entityToModel)
                .map(
                        PurchaseModel::modelToDto
                ).toList());
    }


    public Either<PurchaseResponse, PurchaseDTO> updatePurchase(User user, Long purchaseId, PurchaseRequest updatedPurchaseRequest) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchase = getSinglePurchase(user, purchaseId);
        if (singlePurchase.isLeft()) {
            return singlePurchase;
        }

        PurchaseModel purchaseModel = new PurchaseModel(updatedPurchaseRequest.getDeposit(), updatedPurchaseRequest.isPaid(),
                updatedPurchaseRequest.getStatus(), );

        PurchaseEntity savedPurchase = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));

        PurchaseModel savedModel = PurchaseModel.entityToModel(savedPurchase);
        return Either.right(PurchaseModel.modelToDto(savedModel));
    }

    public PurchaseResponse deletePurchase(User user, Long purchaseId) {
        //checks if purchase and user exists and they belong to each other
        Either<PurchaseResponse, PurchaseDTO> singlePurchaseResult = getSinglePurchase(user, purchaseId);
        if (singlePurchaseResult.isLeft()) {
            return singlePurchaseResult.getLeft();
        }

        //if it exists proceed with deletion
        Optional<PurchaseEntity> purchaseEntity = purchaseRepository.findById(purchaseId);
        if (purchaseEntity.isEmpty()) {
            return new PurchaseResponse(404, "Purchase with id " + purchaseId + " not found");
        }

        try {
            purchaseRepository.delete(purchaseEntity.get());
            return new PurchaseResponse(200, "Purchase deleted successfully");
        } catch (Exception e) {
            return new PurchaseResponse(500, "Internal server error");
        }
    }
}

