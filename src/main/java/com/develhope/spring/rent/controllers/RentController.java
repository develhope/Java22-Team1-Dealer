package com.develhope.spring.rent.controllers;

import com.develhope.spring.rent.DTO.RentDTO;
import com.develhope.spring.rent.request.RentRequest;
import com.develhope.spring.rent.Response.RentResponse;
import com.develhope.spring.rent.services.RentService;
import com.develhope.spring.User.entities.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentController {

    private final RentService rentService;

    @Operation(summary = "Create a rent")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Successfully created rent", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = RentDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Requester or Receiver not found")})
    @PostMapping("/create")
    public ResponseEntity<?> createRent(@RequestBody RentRequest rentRequest, @RequestParam(value = "userId", required = false) Long userId, @AuthenticationPrincipal UserEntity userEntityDetails) {
        Either<RentResponse, RentDTO> result = rentService.createRent(rentRequest, userId, userEntityDetails);

        return result.isRight() ? ResponseEntity.ok(result.get()) : ResponseEntity.status(result.getLeft().getStatusCode()).body(result.getLeft());
    }


    @Operation(summary = "Get a list of rents")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved list of rents", content =
            {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RentDTO.class)))}),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping("/list")
    public ResponseEntity<List<RentDTO>> getRentList(@AuthenticationPrincipal UserEntity userEntityDetails) {
        List<RentDTO> rentList = rentService.getRentList(userEntityDetails);
        if (rentList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rentList);
    }


    @Operation(summary = "Get a rent by ID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved rent by ID", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = RentDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Rent not found")})
    @GetMapping("/find/{id}")
    public ResponseEntity<RentDTO> getRentById(@PathVariable Long id, @AuthenticationPrincipal UserEntity userEntityDetails) {
        RentDTO rentDTO = rentService.getRentById(id, userEntityDetails);
        if (rentDTO != null) {
            return ResponseEntity.ok(rentDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Update rent dates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully updated rent dates", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = RentDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Rent not found")})
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRentDates(@PathVariable Long id, @RequestBody RentRequest rentRequest, @AuthenticationPrincipal UserEntity userEntityDetails) {
        Either<RentResponse, RentDTO> result = rentService.updateRentDates(id, rentRequest, userEntityDetails);
        return result.isLeft() ? ResponseEntity.status(result.getLeft().getStatusCode()).body(result.getLeft()) : ResponseEntity.ok(result.get());
    }


    @Operation(summary = "Delete a rent")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Successfully deleted rent"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Rent not found")})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRent(@PathVariable Long id, @AuthenticationPrincipal UserEntity userEntityDetails) {
        Either<RentResponse, Void> result = rentService.deleteRent(id, userEntityDetails);
        return result.isLeft() ?
                ResponseEntity.status(result.getLeft().getStatusCode()).body(result.getLeft().getMessage()) :
                ResponseEntity.ok().build();
    }

    @Operation(summary = "Pay a rent")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Payment successful", content = {
            @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Rent not found")})
    @PostMapping("/pay/{id}")
    public ResponseEntity<?> payRent(@PathVariable Long id, @AuthenticationPrincipal UserEntity userEntityDetails) {
        Long userId = userEntityDetails.getId();
        Either<RentResponse, String> result = rentService.payRent(id, userId, userEntityDetails);
        if (result.isLeft()) {
            RentResponse errorResponse = result.getLeft();
            return ResponseEntity.status(errorResponse.getStatusCode()).body(errorResponse.getMessage());
        } else {
            String successMessage = result.get();
            return ResponseEntity.ok(successMessage);
        }
    }

    @DeleteMapping("/deleteBooking/{rentId}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long rentId, @AuthenticationPrincipal UserEntity userEntityDetails) {
        Either<RentResponse, String> result = rentService.deleteBooking(rentId, userEntityDetails);
        return result.isRight() ? ResponseEntity.ok(result.get()) : ResponseEntity.status(result.getLeft().getStatusCode()).body(result.getLeft().getMessage());
    }

}
