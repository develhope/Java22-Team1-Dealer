package com.develhope.spring.Purchase.Service;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.Purchase;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    PurchaseRepository purchaseRepository;

   PurchaseDTO createPurchase(Purchase purchase) {
      return purchaseRepository.save(purchase).toDTO();
   }

   PurchaseDTO getSinglePurchase(Long id) {
       return purchaseRepository.findById(id).get().toDTO();
   }

   List<PurchaseDTO> getAllPurchases() {
       return purchaseRepository.findAll().stream().map(Purchase::toDTO).toList();
   }

   PurchaseDTO updatePurchase(Long id, Purchase updatedPurchase) {
       Optional<Purchase> purchaseOptional = purchaseRepository.findById(id);
       if(purchaseOptional.isPresent()) {
           purchaseOptional.get().setDeposit(updatedPurchase.getDeposit());
           purchaseOptional.get().setStatus(updatedPurchase.getStatus());
           purchaseOptional.get().setPaid(updatedPurchase.isPaid());
           return purchaseOptional.get().toDTO();
       } else {
           return null;
       }


   }
}
