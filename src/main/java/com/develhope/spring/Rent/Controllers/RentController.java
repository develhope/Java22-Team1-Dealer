package com.develhope.spring.Rent.Controllers;

import com.develhope.spring.Purchase.DTO.PurchaseDTO;
import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.Rent.Entities.RentEntity;
import com.develhope.spring.Rent.Services.RentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Create a rent with required data")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created rent",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RentDTO.class))}
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Unable to create rent"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User with id {userId} not found"
                    )
            }
    )
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createRent(@PathVariable Long userId, @RequestBody RentDTO rentDTO) {
        RentDTO rentEntity = rentService.createRent(userId, rentDTO);
        if (rentEntity == null)
            return ResponseEntity.badRequest().body("Unable to create rent");

        return ResponseEntity.status(HttpStatus.CREATED).body(rentEntity);
    }

    @Operation(summary = "Get a list of rents by user id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of rents",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RentDTO.class))}
                    )
            }
    )
    @GetMapping("/list/{userId}")
    public ResponseEntity<?> rentList(@PathVariable Long userId) {
        List<RentDTO> rentals = rentService.getRentsByUserId(userId);
        return ResponseEntity.ok(rentals);
    }

    @Operation(summary = "Get a rent by user id and rent id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved rent",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RentDTO.class))}
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Rent with id {rentId} not found for user with id {userId}"
                    )
            }
    )
    @GetMapping("/search/{userId}/{rentId}")
    public ResponseEntity<?> getRentById(@PathVariable Long userId, @PathVariable Long rentId) {
        RentDTO rentEntity = rentService.getRentById(userId, rentId);
        if (rentEntity == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(rentEntity);
    }

    @Operation(summary = "Update rent by id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Updated rent with id {rentId}",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RentDTO.class))}
                    ),
                    @ApiResponse(
                            responseCode ="403",
                            description = "This rent does not belong to the specified user"
                    ),
                    @ApiResponse(
                            responseCode ="404",
                            description = "No rent found with id {rentId}"
                    ),
                    @ApiResponse(
                            responseCode ="404",
                            description = "User with id {userId} not found"
                    )
            }
    )
    @PutMapping("/update/{userId}/{rentId}")
    public ResponseEntity<?> updateRentDates(@PathVariable Long userId, @PathVariable Long rentId, @RequestBody RentDTO rentDTO) {
        RentDTO updatedRentEntity = rentService.updateRentDates(userId, rentId, rentDTO);
        if (updatedRentEntity == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(updatedRentEntity);
    }

    @Operation(summary = "Delete rent by id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Rent deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No rent found with id {rentId} for user with id {userId}"
                    )
            }
    )
    @DeleteMapping("/remove/{userId}/{rentId}")
    public ResponseEntity<?> deleteRent(@PathVariable Long userId, @PathVariable Long rentId) {
        boolean deleted = rentService.deleteRent(userId, rentId);
        if (!deleted)
            return ResponseEntity.notFound().build();

        return ResponseEntity.noContent().build();
    }
}


