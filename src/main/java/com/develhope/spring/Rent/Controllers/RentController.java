package com.develhope.spring.Rent.Controllers;

import com.develhope.spring.Rent.Entities.DTO.ModifyRentDTO;
import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.Rent;
import com.develhope.spring.Rent.Serivices.RentService;
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
        Rent rent = rentService.createRent(userId, rentDTO);
        if (rent == null)
            return ResponseEntity.badRequest().body("Unable to create rent");

        return ResponseEntity.status(HttpStatus.CREATED).body(rent);
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<?> rentList(@PathVariable Long userId) {
        List<Rent> rentals = rentService.getRentsByUserId(userId);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/search/{userId}/{rentId}")
    public ResponseEntity<?> getRentById(@PathVariable Long userId, @PathVariable Long rentId) {
        Rent rent = rentService.getRentById(userId, rentId);
        if (rent == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(rent);
    }

    @PutMapping("/update/{userId}/{rentId}")
    public ResponseEntity<?> updateRentDates(@PathVariable Long userId, @PathVariable Long rentId, @RequestBody ModifyRentDTO modifyRentDTO) {
        Rent updatedRent = rentService.updateRentDates(userId, rentId, modifyRentDTO);
        if (updatedRent == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(updatedRent);
    }

    @DeleteMapping("/remove/{userId}/{rentId}")
    public ResponseEntity<?> deleteRent(@PathVariable Long userId, @PathVariable Long rentId) {
        boolean deleted = rentService.deleteRent(userId, rentId);
        if (!deleted)
            return ResponseEntity.notFound().build();

        return ResponseEntity.noContent().build();
    }
}


