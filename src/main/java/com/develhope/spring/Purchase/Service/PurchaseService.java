package com.develhope.spring.Purchase.Service;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.DTO.PurchaseEntity;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    PurchaseRepository purchaseRepository;

   PurchaseDTO createPurchase(PurchaseEntity purchaseEntity) {
      return purchaseRepository.save(purchaseEntity).toDTO();
   }

   PurchaseDTO getSinglePurchase(Long id) {
       return purchaseRepository.findById(id).get().toDTO();
   }

   List<PurchaseDTO> getAllPurchases() {
       return purchaseRepository.findAll().stream().map(PurchaseEntity::toDTO).toList();
   }

   PurchaseDTO updatePurchase(Long id, PurchaseEntity updatedPurchaseEntity) {
       Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(id);
       if(purchaseOptional.isPresent()) {
           purchaseOptional.get().setDeposit(updatedPurchaseEntity.getDeposit());
           purchaseOptional.get().setStatus(updatedPurchaseEntity.getStatus());
           purchaseOptional.get().setPaid(updatedPurchaseEntity.isPaid());
           return purchaseOptional.get().toDTO();
       } else {
           return null;
       }


   }
}
