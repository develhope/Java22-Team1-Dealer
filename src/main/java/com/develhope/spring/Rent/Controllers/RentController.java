package com.develhope.spring.Rent.Controllers;

import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.Rent.Services.RentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentController {

    @Autowired
    private RentService rentService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createRent(@PathVariable Long userId, @RequestBody RentDTO rentDTO) {
        RentDTO rentEntity = rentService.createRent(userId, rentDTO);
        if (rentEntity == null)
            return ResponseEntity.badRequest().body("Unable to create rent");

        return ResponseEntity.status(HttpStatus.CREATED).body(rentEntity);
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<?> rentList(@PathVariable Long userId) {
        List<RentDTO> rentals = rentService.getRentsByUserId(userId);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/search/{userId}/{rentId}")
    public ResponseEntity<?> getRentById(@PathVariable Long userId, @PathVariable Long rentId) {
        RentDTO rentEntity = rentService.getRentById(userId, rentId);
        if (rentEntity == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(rentEntity);
    }

    @PutMapping("/update/{userId}/{rentId}")
    public ResponseEntity<?> updateRentDates(@PathVariable Long userId, @PathVariable Long rentId, @RequestBody RentDTO rentDTO) {
        RentDTO updatedRentEntity = rentService.updateRentDates(userId, rentId, rentDTO);
        if (updatedRentEntity == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(updatedRentEntity);
    }

    @DeleteMapping("/remove/{userId}/{rentId}")
    public ResponseEntity<?> deleteRent(@PathVariable Long userId, @PathVariable Long rentId) {
        boolean deleted = rentService.deleteRent(userId, rentId);
        if (!deleted)
            return ResponseEntity.notFound().build();

        return ResponseEntity.noContent().build();
    }
}


