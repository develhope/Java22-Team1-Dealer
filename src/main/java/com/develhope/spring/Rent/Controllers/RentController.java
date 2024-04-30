package com.develhope.spring.Rent.Controllers;

import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.Rent.Response.RentResponse;
import com.develhope.spring.Rent.Services.RentService;
import io.vavr.control.Either;
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
        Either<RentResponse, RentDTO> result = rentService.createRent(userId, rentDTO);
        if (result.isLeft())
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());

        return ResponseEntity.status(HttpStatus.CREATED).body(result.get());
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<?> rentList(@PathVariable Long userId) {
        Either<RentResponse, List<RentDTO>> result = rentService.getRentsByUserId(userId);
        if (result.isLeft())
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());

        return ResponseEntity.ok(result.get());
    }

    @GetMapping("/search/{userId}/{rentId}")
    public ResponseEntity<?> getRentById(@PathVariable Long userId, @PathVariable Long rentId) {
        Either<RentResponse, RentDTO> result = rentService.getRentById(userId, rentId);
        if (result.isLeft())
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());

        return ResponseEntity.ok(result.get());
    }

    @PutMapping("/update/{userId}/{rentId}")
    public ResponseEntity<?> updateRentDates(@PathVariable Long userId, @PathVariable Long rentId, @RequestBody RentDTO rentDTO) {
        Either<RentResponse, RentDTO> result = rentService.updateRentDates(userId, rentId, rentDTO);
        if (result.isLeft())
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());

        return ResponseEntity.ok(result.get());
    }

    @DeleteMapping("/remove/{userId}/{rentId}")
    public ResponseEntity<?> deleteRent(@PathVariable Long userId, @PathVariable Long rentId) {
        Either<RentResponse, Boolean> result = rentService.deleteRent(userId, rentId);
        if (result.isLeft())
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());

        return ResponseEntity.noContent().build();
    }
}


