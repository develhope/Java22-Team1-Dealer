package com.develhope.spring.Rent.Controllers;

import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Request.RentRequest;
import com.develhope.spring.Rent.Response.RentResponse;
import com.develhope.spring.Rent.Services.RentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rents")
@RequiredArgsConstructor
public class RentController {

    private final RentService rentService;

    @PostMapping
    public ResponseEntity<?> createRent(@RequestBody RentRequest rentRequest) {
        Either<RentResponse, RentDTO> result = rentService.createRent(rentRequest);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getStatusCode()).body(result.getLeft());
        }
    }

    @GetMapping
    public List<RentDTO> getRentList(Principal principal) {
        return rentService.getRentList(principal);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RentDTO> getRentById(@PathVariable Long id, Principal principal) {
        RentDTO rentDTO = rentService.getRentById(id, principal);
        if (rentDTO != null) {
            return ResponseEntity.ok(rentDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRentDates(@PathVariable Long id, @RequestBody RentRequest rentRequest, Principal principal) {
        Either<RentResponse, RentDTO> result = rentService.updateRentDates(id, rentRequest, principal);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getStatusCode()).body(result.getLeft());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRent(@PathVariable Long id, Principal principal) {
        Either<RentResponse, Void> result = rentService.deleteRent(id, principal);
        if (result.isRight()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(result.getLeft().getStatusCode()).body(result.getLeft());
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<String> payRent(@PathVariable Long id, Principal principal) {
        Either<RentResponse, String> result = rentService.payRent(id, principal);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getStatusCode()).body(result.getLeft().getMessage());
        }
    }
}


