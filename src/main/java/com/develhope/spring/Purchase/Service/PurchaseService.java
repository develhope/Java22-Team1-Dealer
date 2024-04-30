package com.develhope.spring.Purchase.Service;

import com.develhope.spring.Purchase.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.PurchaseEntity;
import com.develhope.spring.Purchase.Model.PurchaseModel;
import com.develhope.spring.Purchase.Request.PurchaseRequest;
import com.develhope.spring.Purchase.Response.PurchaseResponse;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    PurchaseRepository purchaseRepository;

    public PurchaseDTO createPurchase(PurchaseRequest purchaseRequest) {
        PurchaseModel purchaseModel = new PurchaseModel(purchaseRequest.getDeposit(), purchaseRequest.isPaid(), purchaseRequest.getStatus(), purchaseRequest.getOrder());

       PurchaseEntity result = purchaseRepository.save(PurchaseModel.modelToEntity(purchaseModel));
       PurchaseModel resultModel = PurchaseModel.entityToModel(result);
       return PurchaseModel.modelToDto(resultModel);
    }

    public Either<PurchaseResponse, PurchaseDTO> getSinglePurchase(Long id) {
        Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(id);
        if (purchaseOptional.isPresent()) {
            PurchaseModel purchaseModel = PurchaseModel.entityToModel(purchaseOptional.get());
            return Either.right(PurchaseModel.modelToDto(purchaseModel));

        } else {
            return Either.left(new PurchaseResponse(404, "Purchase with id " + id + " not found"));
        }
    }

    public List<PurchaseDTO> getAllPurchases() {
        return purchaseRepository.findAll().stream().map(purchaseEntity -> {
            PurchaseModel purchaseModel = PurchaseModel.entityToModel(purchaseEntity);
            return PurchaseModel.modelToDto(purchaseModel);
        }).toList();
    }

    public Either<PurchaseResponse, PurchaseDTO> updatePurchase(Long id, PurchaseModel updatedPurchaseModel) {
        Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(id);
        if (purchaseOptional.isPresent()) {
            PurchaseModel purchaseModel = PurchaseModel.entityToModel(purchaseOptional.get());
            purchaseModel.setDeposit(updatedPurchaseModel.getDeposit());
            purchaseModel.setStatus(updatedPurchaseModel.getStatus());
            purchaseModel.setPaid(updatedPurchaseModel.isPaid());
            return Either.right(PurchaseModel.modelToDto(purchaseModel));
        } else {
            return Either.left(new PurchaseResponse(
                    404,
                    "Purchase with id" + id + "not found"
            ));
        }
    }

    public PurchaseResponse deletePurchase(Long id) {
        Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(id);
        if (purchaseOptional.isPresent()) {
            purchaseRepository.deleteById(id);
            return new PurchaseResponse(200, "Purchase with id " + id + " has been deletes successfully");
        } else {
            return new PurchaseResponse(404, "Purchase with id " + id + " not found");
        }
    }
}

