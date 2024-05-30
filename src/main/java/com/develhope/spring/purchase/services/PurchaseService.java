package com.develhope.spring.purchase.services;

import com.develhope.spring.dealershipstatistics.service.DealershipStatisticsService;
import com.develhope.spring.purchase.DTO.PurchaseDTO;
import com.develhope.spring.purchase.model.PurchaseModel;
import com.develhope.spring.purchase.entities.PurchaseEntity;
import com.develhope.spring.purchase.entities.PurchasesLinkEntity;
import com.develhope.spring.purchase.repositories.PurchaseRepository;
import com.develhope.spring.purchase.repositories.PurchasesLinkRepository;
import com.develhope.spring.purchase.request.PurchaseRequest;
import com.develhope.spring.purchase.response.PurchaseResponse;
import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.vehicles.model.VehicleModel;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
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
    @Autowired
    DealershipStatisticsService dealershipStatisticsService;

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

        PurchaseModel purchaseModel = new PurchaseModel(
                purchaseRequest.getIsPaid(),
                VehicleModel.entityToModel(vehicleEntity.get()),
                LocalDate.now()
        );
        PurchaseEntity savedEntity = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));
        dealershipStatisticsService.updatePurchaseStatistics(savedEntity.getVehicle());

        updateVehicleStatus(savedEntity.getVehicle(), VehicleStatus.SOLD);

        purchasesLinkRepository.save(new PurchasesLinkEntity(buyer, savedEntity, buyer.getId().equals(seller.getId()) ? null : seller));

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

    private void updateVehicleStatus(VehicleEntity vehicle, VehicleStatus vehicleStatus) {
        vehicle.setVehicleStatus(vehicleStatus);
        vehicleRepository.save(vehicle);
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

        PurchaseDTO purchaseDTO = singlePurchase.get();

        purchaseDTO.setIsPaid(updatedPurchaseRequest.getIsPaid() != null ? updatedPurchaseRequest.getIsPaid() : purchaseDTO.getIsPaid());

        if (updatedPurchaseRequest.getVehicleId() != null) {
            vehicleRepository.findById(updatedPurchaseRequest.getVehicleId())
                    .map(vehicleEntity -> VehicleModel.modelToDTO(VehicleModel.entityToModel(vehicleEntity)))
                    .ifPresent(purchaseDTO::setVehicle);
        }

        PurchaseEntity savedPurchase = purchaseRepository.save(PurchaseModel.modelToEntity(PurchaseModel.dtoToModel(purchaseDTO)));

        if (!savedPurchase.getVehicle().getVehicleId().equals(purchaseDTO.getVehicle().getVehicleId())) {
            VehicleEntity oldVehicle = VehicleModel.modelToEntity(VehicleModel.DTOtoModel(purchaseDTO.getVehicle()));
            dealershipStatisticsService.removePurchaseStatistics(oldVehicle);
            dealershipStatisticsService.updatePurchaseStatistics(savedPurchase.getVehicle());
        }

        PurchaseModel savedModel = PurchaseModel.entityToModel(savedPurchase);
        return Either.right(PurchaseModel.modelToDto(savedModel));
    }

    @Transactional
    public PurchaseResponse delete(UserEntity userEntity, Long purchaseId) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchaseResult = getSingle(userEntity, purchaseId);
        if (singlePurchaseResult.isLeft()) {
            return singlePurchaseResult.getLeft();
        }

        PurchaseEntity purchaseEntity = PurchaseModel.modelToEntity(PurchaseModel.dtoToModel(singlePurchaseResult.get()));

        try {
            VehicleEntity vehicleEntity = purchaseEntity.getVehicle();
            updateVehicleStatus(vehicleEntity, VehicleStatus.PURCHASABLE);

            purchasesLinkRepository.delete(purchasesLinkRepository.findByPurchase_PurchaseId(purchaseId));
            purchaseEntity.setIsPaid(false);
            purchaseEntity.setVehicle(null);
            purchaseRepository.save(purchaseEntity);
            return new PurchaseResponse(200, "Purchase deleted successfully");
        } catch (Exception e) {
            return new PurchaseResponse(500, "Internal server error");
        }
    }
}

