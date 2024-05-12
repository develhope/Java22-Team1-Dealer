package com.develhope.spring.Purchase.Services;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.DTO.PurchaseModel;
import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import com.develhope.spring.Purchase.Request.PurchaseRequest;
import com.develhope.spring.Purchase.Response.PurchaseResponse;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
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
    VehicleRepository vehicleRepository;

    public Either<PurchaseResponse, PurchaseDTO> createPurchase(UserEntity buyer, PurchaseRequest purchaseRequest) {
        //check if user has the necessary role
        if (buyer.getUserType() != UserTypes.BUYER && buyer.getUserType() != UserTypes.ADMIN) {
            return Either.left(new PurchaseResponse(403, "This user does not have permission to create a purchase"));
        }

        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(purchaseRequest.getVehicleId());
        if (vehicleEntity.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "No vehicle found with such Id"));
        }

        //check if deposit is not negative
        if (purchaseRequest.getDeposit() < 0) {
            return Either.left(new PurchaseResponse(400, "Deposit cannot be negative"));
        }

        PurchaseModel purchaseModel = new PurchaseModel(purchaseRequest.getDeposit(), purchaseRequest.getIsPaid(),
                PurchaseStatus.convertFromString(purchaseRequest.getStatus()), vehicleEntity.get(), buyer);
        PurchaseEntity result = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));

        PurchaseModel resultModel = PurchaseModel.entityToModel(result);
        return Either.right(PurchaseModel.modelToDto(resultModel));
    }

    public Either<PurchaseResponse, PurchaseDTO> getSinglePurchase(UserEntity userEntity, Long purchadeId) {
        Optional<PurchaseEntity> purchaseEntity = userEntity.getPurchaseEntities().stream().filter(pe -> pe.getPurchaseId().equals(purchadeId)).findFirst();
        if (purchaseEntity.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "Purchase not found"));
        }

        PurchaseModel purchaseModel = PurchaseModel.entityToModel(purchaseEntity.get());
        return Either.right(PurchaseModel.modelToDto(purchaseModel));
    }

    public Either<PurchaseResponse, List<PurchaseDTO>> getAllPurchases(UserEntity userEntity) {
        List<PurchaseEntity> userPurchase = userEntity.getPurchaseEntities();
        if (userPurchase.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "Purchases not found"));
        }

        return Either.right(userPurchase.stream()
                .map(
                        PurchaseModel::entityToModel)
                .map(
                        PurchaseModel::modelToDto
                ).toList());
    }


    public Either<PurchaseResponse, PurchaseDTO> updatePurchase(UserEntity userEntity, Long purchaseId, PurchaseRequest updatedPurchaseRequest) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchase = getSinglePurchase(userEntity, purchaseId);
        if (singlePurchase.isLeft()) {
            return singlePurchase;
        }

        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(updatedPurchaseRequest.getVehicleId());

        singlePurchase.get().setDeposit(updatedPurchaseRequest.getDeposit() == null ? singlePurchase.get().getDeposit() : updatedPurchaseRequest.getDeposit());
        singlePurchase.get().setIsPaid(updatedPurchaseRequest.getIsPaid() == null ? singlePurchase.get().getIsPaid() : updatedPurchaseRequest.getIsPaid());
        singlePurchase.get().setStatus(updatedPurchaseRequest.getStatus() == null ? singlePurchase.get().getStatus() : PurchaseStatus.convertFromString(updatedPurchaseRequest.getStatus()));
        singlePurchase.get().setVehicleEntity(vehicleEntity.orElseGet(() -> singlePurchase.get().getVehicleEntity()));


        PurchaseEntity savedPurchase = purchaseRepository.save(PurchaseModel.modelToEntity(PurchaseModel.dtoToModel(singlePurchase.get())));

        PurchaseModel savedModel = PurchaseModel.entityToModel(savedPurchase);
        return Either.right(PurchaseModel.modelToDto(savedModel));
    }

    public PurchaseResponse deletePurchase(UserEntity userEntity, Long purchaseId) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchaseResult = getSinglePurchase(userEntity, purchaseId);
        if (singlePurchaseResult.isLeft()) {
            return singlePurchaseResult.getLeft();
        }

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

