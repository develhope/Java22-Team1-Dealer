package com.develhope.spring.Purchase.Service;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.DTO.PurchaseModel;
import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import com.develhope.spring.Purchase.Request.PurchaseRequest;
import com.develhope.spring.Purchase.Response.PurchaseResponse;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
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

    public Either<PurchaseResponse, PurchaseDTO> createPurchase(Long userId, PurchaseRequest purchaseRequest) {
        //check if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new PurchaseResponse(404, "User not found"));
        }

        User user = userOptional.get();
        //check if user has the necessary role
        if (user.getUserType() != UserTypes.BUYER && user.getUserType() != UserTypes.ADMIN) {
            return Either.left(new PurchaseResponse(403, "This user does not have permission to create a purchase"));
        }

        //check if deposit is not negative
        if (purchaseRequest.getDeposit() < 0) {
            return Either.left(new PurchaseResponse(400, "Deposit cannot be negative"));
        }

        PurchaseModel purchaseModel = new PurchaseModel(purchaseRequest.getDeposit(), purchaseRequest.isPaid(),
                purchaseRequest.getStatus(), purchaseRequest.getOrderEntity());
        PurchaseEntity result = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));

        PurchaseModel resultModel = PurchaseModel.entityToModel(result);
        return Either.right(PurchaseModel.modelToDto(resultModel));
    }

    public Either<PurchaseResponse, PurchaseDTO> getSinglePurchase(Long userId, Long purchadeId) {
        //checks if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new PurchaseResponse(419, "User with id " + userId + "not found"));
        }
        //checks if purchase exists
        Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(purchadeId);
        if (purchaseOptional.isEmpty()) {
            return Either.left(new PurchaseResponse(420, "Purchase with id " + purchadeId + " not found"));
        }

        //checks if purchase belongs to user
        PurchaseEntity purchaseEntity = purchaseOptional.get();
        OrderEntity orderEntity = purchaseEntity.getOrderEntity();
        User user = userOptional.get();
        if (!(user.getOrderEntities().contains(orderEntity))) {
            return Either.left(new PurchaseResponse(403, "This purchase does not belong to the specified user"));
        }

        //successfully returns purchase
        PurchaseModel purchaseModel = PurchaseModel.entityToModel(purchaseOptional.get());
        return Either.right(PurchaseModel.modelToDto(purchaseModel));
    }

    public Either<PurchaseResponse, List<PurchaseDTO>> getAllPurchases(Long userId) {
        //checks if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return Either.left(new PurchaseResponse(419, "User with id " + userId + "found"));
        }

        List<OrderEntity> userOrderEntities = userOptional.get().getOrderEntities();

        List<PurchaseEntity> userPurchase = userOrderEntities.stream().flatMap(order -> order.getPurchases().stream()).toList();

        return Either.right(userPurchase.stream()
                .map(
                        PurchaseModel::entityToModel)
                .map(
                        PurchaseModel::modelToDto
                ).toList());
    }


    public Either<PurchaseResponse, PurchaseDTO> updatePurchase(Long userId, Long purchaseId, PurchaseRequest updatedPurchaseRequest) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchase = getSinglePurchase(userId, purchaseId);
        if (singlePurchase.isLeft()) {
            return singlePurchase;
        }

        PurchaseModel purchaseModel = new PurchaseModel(updatedPurchaseRequest.getDeposit(), updatedPurchaseRequest.isPaid(),
                updatedPurchaseRequest.getStatus(), updatedPurchaseRequest.getOrderEntity());

        PurchaseEntity savedPurchase = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));

        PurchaseModel savedModel = PurchaseModel.entityToModel(savedPurchase);
        return Either.right(PurchaseModel.modelToDto(savedModel));
    }

    public PurchaseResponse deletePurchase(Long userId, Long purchaseId) {
        //checks if purchase and user exists and they belong to each other
        Either<PurchaseResponse, PurchaseDTO> singlePurchaseResult = getSinglePurchase(userId, purchaseId);
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

