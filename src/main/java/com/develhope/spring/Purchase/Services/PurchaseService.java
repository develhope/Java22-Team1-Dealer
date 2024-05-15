package com.develhope.spring.Purchase.Services;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.DTO.PurchaseModel;
import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Purchase.Entities.PurchasesLinkEntity;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import com.develhope.spring.Purchase.Repositories.PurchasesLinkRepository;
import com.develhope.spring.Purchase.Request.PurchaseRequest;
import com.develhope.spring.Purchase.Response.PurchaseResponse;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleModel;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import io.vavr.control.Either;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    PurchasesLinkRepository purchasesLinkRepository;

    public Either<PurchaseResponse, PurchaseDTO> create(UserEntity seller, @Nullable Long buyerId, PurchaseRequest purchaseRequest) {
        if (purchaseRequest == null || purchaseRequest.getDeposit() < 0) {
            return Either.left(new PurchaseResponse(400, "Invalid input parameters"));
        }

        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(purchaseRequest.getVehicleId());
        if (vehicleEntity.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "No vehicle found with such Id"));
        }
        if(vehicleEntity.get().getVehicleStatus() != VehicleStatus.PURCHASABLE) {
            return Either.left(new PurchaseResponse(403, "Vehicle is not purchasable"));
        }

        UserEntity buyer;
        if (buyerId != null && seller.getUserType() == UserTypes.ADMIN || seller.getUserType() == UserTypes.SELLER) {
            Optional<UserEntity> optionalBuyer = userRepository.findById(buyerId);
            if(optionalBuyer.isEmpty()) {
                return Either.left(new PurchaseResponse(404, "Specified buyer not found"));
            }
            buyer = optionalBuyer.get();
        } else {
            buyer = seller;
        }

        PurchaseModel purchaseModel = new PurchaseModel(purchaseRequest.getDeposit(), purchaseRequest.getIsPaid(),
                PurchaseStatus.convertFromString(purchaseRequest.getStatus()), VehicleModel.entityToModel(vehicleEntity.get()));
        PurchaseEntity savedEntity = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));
        purchasesLinkRepository.saveAndFlush(new PurchasesLinkEntity(buyer, savedEntity));
        PurchaseModel resultModel = PurchaseModel.entityToModel(savedEntity);
        return Either.right(PurchaseModel.modelToDto(resultModel));
    }

    public Either<PurchaseResponse, PurchaseDTO> getSingle(UserEntity userEntity, Long purchadeId) {
        Optional<PurchaseEntity> purchaseEntity = purchaseRepository.findById(purchadeId);
        if (purchaseEntity.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "Purchase with id " + purchadeId + " not found"));
        }

        if (userEntity.getUserType() != UserTypes.ADMIN) {
            List<PurchaseEntity> userPurchases = purchasesLinkRepository.findByBuyer_Id(userEntity.getId()).stream().map(PurchasesLinkEntity::getPurchase).toList();

            if (userPurchases.stream().noneMatch(pe -> pe.getPurchaseId().equals(purchaseEntity.get().getPurchaseId()))) {
                return Either.left(new PurchaseResponse(404, "Purchase does not belong to specified user"));
            }
        }

        PurchaseModel purchaseModel = PurchaseModel.entityToModel(purchaseEntity.get());
        return Either.right(PurchaseModel.modelToDto(purchaseModel));
    }

    public Either<PurchaseResponse, List<PurchaseDTO>> getAll(UserEntity userEntity) {
        List<PurchaseEntity> userPurchase = purchasesLinkRepository.findByBuyer_Id(userEntity.getId()).stream().map(PurchasesLinkEntity::getPurchase).toList();
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


    public Either<PurchaseResponse, PurchaseDTO> update(UserEntity userEntity, Long purchaseId, PurchaseRequest updatedPurchaseRequest) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchase = getSingle(userEntity, purchaseId);
        if (singlePurchase.isLeft()) {
            return Either.left(singlePurchase.getLeft());
        }

        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(updatedPurchaseRequest.getVehicleId());

        singlePurchase.get().setDeposit(updatedPurchaseRequest.getDeposit() == null ? singlePurchase.get().getDeposit() : updatedPurchaseRequest.getDeposit());
        singlePurchase.get().setIsPaid(updatedPurchaseRequest.getIsPaid() == null ? singlePurchase.get().getIsPaid() : updatedPurchaseRequest.getIsPaid());
        singlePurchase.get().setStatus(updatedPurchaseRequest.getStatus() == null ? singlePurchase.get().getStatus() : PurchaseStatus.convertFromString(updatedPurchaseRequest.getStatus()));
        singlePurchase.get().setVehicle(vehicleEntity.isEmpty() ? singlePurchase.get().getVehicle() : vehicleEntity.map(ve -> VehicleModel.modelToDTO(VehicleModel.entityToModel(ve))).get());


        PurchaseEntity savedPurchase = purchaseRepository.save(PurchaseModel.modelToEntity(PurchaseModel.dtoToModel(singlePurchase.get())));

        PurchaseModel savedModel = PurchaseModel.entityToModel(savedPurchase);
        return Either.right(PurchaseModel.modelToDto(savedModel));
    }

    public PurchaseResponse delete(UserEntity userEntity, Long purchaseId) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchaseResult = getSingle(userEntity, purchaseId);
        if (singlePurchaseResult.isLeft()) {
            return singlePurchaseResult.getLeft();
        }

        Optional<PurchaseEntity> purchaseEntity = purchaseRepository.findById(purchaseId);
        if (purchaseEntity.isEmpty()) {
            return new PurchaseResponse(404, "Purchase with id " + purchaseId + " not found");
        }

        try {
            purchasesLinkRepository.delete(purchasesLinkRepository.findByPurchase_PurchaseId(purchaseId));
            return new PurchaseResponse(200, "Purchase deleted successfully");
        } catch (Exception e) {
            return new PurchaseResponse(500, "Internal server error");
        }
    }
}

