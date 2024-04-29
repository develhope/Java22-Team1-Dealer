package com.develhope.spring.Purchase.Controllers;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Service.PurchaseService;
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
    public ResponseEntity<List<PurchaseDTO>> getAll() {
        ResponseEntity<List<PurchaseDTO>> result = purchaseService.getAllPurchases();
        return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSingle(@PathVariable Long id) {
        ResponseEntity<?> result =purchaseService.getSinglePurchase(id);
        return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
    }

    @PostMapping("/")
    public ResponseEntity<PurchaseDTO> create(@RequestBody PurchaseDTO purchaseDTO) {
        return ResponseEntity.ok().body(purchaseService.createPurchase(purchaseDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PurchaseDTO purchaseDTO) {
      ResponseEntity<?> result = purchaseService.updatePurchase(id, purchaseDTO);
        return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
    }

    @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(@PathVariable Long id) {
        ResponseEntity<String> result = purchaseService.deletePurchase(id);
        return ResponseEntity.status(result.getStatusCode()).body(result.getBody());
        }

}
