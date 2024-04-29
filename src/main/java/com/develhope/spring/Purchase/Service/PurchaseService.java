package com.develhope.spring.Purchase.Service;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.DTO.PurchaseEntity;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    @Autowired
    PurchaseRepository purchaseRepository;

    public PurchaseDTO createPurchase(PurchaseDTO purchaseDTO) {
        PurchaseEntity purchaseEntity = purchaseDTO.toModel().toEntity();
        purchaseRepository.save(purchaseEntity);
        return purchaseEntity.toModel().toDto();
    }

    public ResponseEntity<?> getSinglePurchase(Long id) {
        Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(id);
        if (purchaseOptional.isPresent()) {
            purchaseRepository.deleteById(id);
            return ResponseEntity.ok("Purchase with id " + id + " deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase with id " + id + " not found");
        }
    }

   public ResponseEntity<List<PurchaseDTO>> getAllPurchases() {
      return  ResponseEntity.ok().body(purchaseRepository.findAll().stream().map(purchaseEntity -> {
             return purchaseEntity.toModel().toDto();
         }).toList());
    }

    public ResponseEntity<?> updatePurchase(Long id, PurchaseDTO updatedPurchaseDTO) {
        Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(id);
        if (purchaseOptional.isPresent()) {
            purchaseOptional.get().setDeposit(updatedPurchaseDTO.getDeposit());
            purchaseOptional.get().setStatus(updatedPurchaseDTO.getStatus());
            purchaseOptional.get().setPaid(updatedPurchaseDTO.isPaid());
            return ResponseEntity.ok().body(purchaseOptional.get().toModel());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase with id " + id + " not found");
        }
    }

    public ResponseEntity<String> deletePurchase(Long id) {
        Optional<PurchaseEntity> purchaseOptional = purchaseRepository.findById(id);
        if (purchaseOptional.isPresent()) {
            purchaseRepository.deleteById(id);
            return ResponseEntity.ok("Purchase with id " + id + " deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Purchase with id " + id + " not found");
        }
    }
}

