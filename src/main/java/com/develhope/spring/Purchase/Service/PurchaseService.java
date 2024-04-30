package com.develhope.spring.Purchase.Service;

import com.develhope.spring.Purchase.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Purchase.Model.PurchaseModel;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import com.develhope.spring.Purchase.Request.PurchaseRequest;
import com.develhope.spring.Purchase.Response.PurchaseResponse;
import com.develhope.spring.User.Entities.User;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.order.Entities.Order;
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
        //checks if user exists
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            //checks if deposit is not negative
            if (purchaseRequest.getDeposit() < 0) {
                return Either.left(new PurchaseResponse(400, "Deposit cannot be negative"));
            }
            PurchaseModel purchaseModel = new PurchaseModel(purchaseRequest.getDeposit(), purchaseRequest.isPaid(),
                    purchaseRequest.getStatus(), purchaseRequest.getOrder());

            PurchaseEntity result = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));

            PurchaseModel resultModel = PurchaseModel.entityToModel(result);
            return Either.right(PurchaseModel.modelToDto(resultModel));
        } else {
            return Either.left(new PurchaseResponse(404, "User not found"));
        }


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
        Order order = purchaseEntity.getOrder();
        User user = userOptional.get();
        if (!(user.getOrders().contains(order))) {
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

        List<Order> userOrders = userOptional.get().getOrders();

        List<PurchaseEntity> userPurchase = userOrders.stream().flatMap(order -> order.getPurchases().stream()).toList();

        return Either.right(userPurchase.stream()
                .map(
                        PurchaseModel::entityToModel)
                .map(
                        PurchaseModel::modelToDto
                ).toList());
    }


    public Either<PurchaseResponse, PurchaseDTO> updatePurchase(Long userId, Long purchadeId, PurchaseModel updatedPurchaseModel) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchase = getSinglePurchase(userId, purchadeId);
        if (singlePurchase.isLeft()) {
            return singlePurchase;
        }
        PurchaseModel purchaseModel = PurchaseModel.dtoToModel(singlePurchase.get());

        purchaseModel.setDeposit(updatedPurchaseModel.getDeposit());
        purchaseModel.setStatus(updatedPurchaseModel.getStatus());
        purchaseModel.setPaid(updatedPurchaseModel.isPaid());

        PurchaseEntity savedPurchase = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));

        PurchaseModel savedModel = PurchaseModel.entityToModel(savedPurchase);

        return Either.right(PurchaseModel.modelToDto(savedModel));

    }

    public Either<PurchaseResponse, PurchaseDTO> deletePurchase(Long userId, Long purchadeId) {
        Either<PurchaseResponse, PurchaseDTO> singlePurchase = getSinglePurchase(userId, purchadeId);
        if (singlePurchase.isLeft()) {
            return singlePurchase;
        }

        PurchaseModel foundPurchase = PurchaseModel.dtoToModel(singlePurchase.get());

    }
}

