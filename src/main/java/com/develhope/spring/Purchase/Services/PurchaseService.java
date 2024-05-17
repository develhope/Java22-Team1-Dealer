package com.develhope.spring.Purchase.Services;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.DTO.PurchaseModel;
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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    @Transactional
    public Either<PurchaseResponse, PurchaseDTO> create(UserEntity seller, @Nullable Long buyerId, PurchaseRequest purchaseRequest) {
        if (purchaseRequest == null) {
            return Either.left(new PurchaseResponse(400, "Invalid input parameters"));
        }

        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(purchaseRequest.getVehicleId());
        if (vehicleEntity.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "No vehicle found with such Id"));
        }

        if (vehicleEntity.get().getVehicleStatus() != VehicleStatus.PURCHASABLE) {
            return Either.left(new PurchaseResponse(403, "Vehicle is not purchasable"));
        }

        UserEntity buyer = resolveBuyer(seller, buyerId);
        if (buyer == null) {
            return Either.left(new PurchaseResponse(404, "Specified buyer not found"));
        }

        vehicleEntity.get().setVehicleStatus(VehicleStatus.SOLD);
        vehicleRepository.save(vehicleEntity.get());

        PurchaseModel purchaseModel = new PurchaseModel(
                purchaseRequest.getIsPaid(),
                VehicleModel.entityToModel(vehicleEntity.get()),
                LocalDate.now()
        );
        PurchaseEntity savedEntity = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));

        if (!buyer.getId().equals(seller.getId())) {
            purchasesLinkRepository.saveAndFlush(new PurchasesLinkEntity(buyer, savedEntity, seller));
        } else {
            purchasesLinkRepository.saveAndFlush(new PurchasesLinkEntity(buyer, savedEntity));
        }

        PurchaseModel resultModel = PurchaseModel.entityToModel(savedEntity);
        return Either.right(PurchaseModel.modelToDto(resultModel));
    }

    private UserEntity resolveBuyer(UserEntity seller, @Nullable Long buyerId) {
        if (buyerId != null && (seller.getUserType() == UserTypes.ADMIN || seller.getUserType() == UserTypes.SELLER)) {
            Optional<UserEntity> optionalBuyer = userRepository.findById(buyerId);
            return optionalBuyer.orElse(null);
        } else {
            return seller;
        }
    }

    public Either<PurchaseResponse, PurchaseDTO> getSingle(UserEntity userEntity, Long purchaseId) {
        Optional<PurchaseEntity> purchaseEntity = purchaseRepository.findById(purchaseId);
        if (purchaseEntity.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "Purchase with id " + purchaseId + " not found"));
        }

        if (userEntity.getUserType() != UserTypes.ADMIN) {
            List<PurchaseEntity> userPurchases = purchasesLinkRepository.findByBuyer_Id(userEntity.getId()).stream()
                    .map(PurchasesLinkEntity::getPurchase).toList();

            if (userPurchases.stream().noneMatch(pe -> pe.getPurchaseId().equals(purchaseEntity.get().getPurchaseId()))) {
                return Either.left(new PurchaseResponse(404, "Purchase does not belong to specified user"));
            }
        }

        PurchaseModel purchaseModel = PurchaseModel.entityToModel(purchaseEntity.get());
        return Either.right(PurchaseModel.modelToDto(purchaseModel));
    }

    public Either<PurchaseResponse, List<PurchaseDTO>> getAll(UserEntity userEntity) {
        List<PurchaseEntity> userPurchases = purchasesLinkRepository.findByBuyer_Id(userEntity.getId()).stream()
                .map(PurchasesLinkEntity::getPurchase).toList();

        if (userPurchases.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "Purchases not found"));
        }

        List<PurchaseDTO> purchaseDTOs = userPurchases.stream()
                .map(PurchaseModel::entityToModel)
                .map(PurchaseModel::modelToDto)
                .toList();

        return Either.right(purchaseDTOs);
    }

    @Transactional
    public Either<PurchaseResponse, PurchaseDTO> update(UserEntity userEntity, Long purchaseId, PurchaseRequest updatedPurchaseRequest) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchase = getSingle(userEntity, purchaseId);
        if (singlePurchase.isLeft()) {
            return Either.left(singlePurchase.getLeft());
        }

        Optional<VehicleEntity> vehicleEntity = vehicleRepository.findById(updatedPurchaseRequest.getVehicleId());
        if (vehicleEntity.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "Vehicle not found with such Id"));
        }

        PurchaseDTO purchaseDTO = singlePurchase.get();
        purchaseDTO.setIsPaid(updatedPurchaseRequest.getIsPaid() != null ? updatedPurchaseRequest.getIsPaid() : purchaseDTO.getIsPaid());
        purchaseDTO.setVehicle(VehicleModel.modelToDTO(VehicleModel.entityToModel(vehicleEntity.get())));

        PurchaseEntity savedPurchase = purchaseRepository.save(PurchaseModel.modelToEntity(PurchaseModel.dtoToModel(purchaseDTO)));
        PurchaseModel savedModel = PurchaseModel.entityToModel(savedPurchase);

        return Either.right(PurchaseModel.modelToDto(savedModel));
    }

    @Transactional
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
            VehicleEntity vehicleEntity = purchaseEntity.get().getVehicle();
            vehicleEntity.setVehicleStatus(VehicleStatus.PURCHASABLE);
            vehicleRepository.save(vehicleEntity);

            purchasesLinkRepository.delete(purchasesLinkRepository.findByPurchase_PurchaseId(purchaseId));
            purchaseRepository.deleteById(purchaseId);

            return new PurchaseResponse(200, "Purchase deleted successfully");
        } catch (Exception e) {
            return new PurchaseResponse(500, "Internal server error");
        }
    }
}

