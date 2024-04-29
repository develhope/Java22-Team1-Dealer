package com.develhope.spring.Purchase.Service;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.Entity.PurchaseEntity;
import com.develhope.spring.Purchase.Entities.Model.PurchaseModel;
import com.develhope.spring.Purchase.Entities.Response.PurchaseResponse;
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

    public PurchaseDTO createPurchase(PurchaseDTO purchaseDTO) {
        PurchaseEntity purchaseEntity = purchaseDTO.toModel().toEntity();
       return purchaseRepository.save(purchaseEntity).toModel().toDto();
    }

    public Either<PurchaseResponse, PurchaseDTO> getSinglePurchase(Long id) {
        Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(id);
        if (purchaseOptional.isPresent()) {
           return Either.right(purchaseRepository.findById(id).get().toModel().toDto());

        } else {
            return Either.left(new PurchaseResponse(404, "Purchase with id " + id + " not found"));
        }
    }

   public List<PurchaseDTO> getAllPurchases() {
      return  purchaseRepository.findAll().stream().map(purchaseEntity -> {
             return purchaseEntity.toModel().toDto();
         }).toList();
    }

    public Either<PurchaseResponse, PurchaseDTO> updatePurchase(Long id, PurchaseModel updatedPurchaseModel) {
        Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(id);
        if (purchaseOptional.isPresent()) {
            PurchaseModel purchaseModel = purchaseOptional.get().toModel();
            purchaseModel.setDeposit(updatedPurchaseModel.getDeposit());
            purchaseModel.setStatus(updatedPurchaseModel.getStatus());
            purchaseModel.setPaid(updatedPurchaseModel.isPaid());
            return Either.right(purchaseModel.toDto());
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
            return new PurchaseResponse(404,"Purchase with id " + id + " not found");
        }
    }
}

