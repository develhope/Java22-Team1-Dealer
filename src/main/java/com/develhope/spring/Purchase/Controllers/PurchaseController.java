package com.develhope.spring.Purchase.Controllers;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.Response.PurchaseResponse;
import com.develhope.spring.Purchase.Service.PurchaseService;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        List<PurchaseDTO> result = purchaseService.getAllPurchases();
        if(result.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSingle(@PathVariable Long id) {
        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.getSinglePurchase(id);
        if (result.isRight()) {
            return ResponseEntity.ok().body(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<PurchaseDTO> create(@RequestBody PurchaseDTO purchaseDTO) {
        return ResponseEntity.ok().body(purchaseService.createPurchase(purchaseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PurchaseDTO purchaseDTO) {
        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.updatePurchase(id, purchaseDTO.toModel());

        if (result.isRight()) {
            return ResponseEntity.ok().body(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        PurchaseResponse result = purchaseService.deletePurchase(id);
        return ResponseEntity.status(result.getCode()).body(result.getMessage());
    }

}
