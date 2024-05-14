package com.develhope.spring.Purchase.Controllers;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Request.PurchaseRequest;
import com.develhope.spring.Purchase.Response.PurchaseResponse;
import com.develhope.spring.Purchase.Services.PurchaseService;
import com.develhope.spring.User.Entities.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dealer/purchase")
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @Operation(summary = "Create a purchase with required data")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully created purchase",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Deposit is negative"),
            @ApiResponse(responseCode = "404", description = "User with id{userId} not found")})
    @PostMapping("/create")
    public ResponseEntity<?> create(@AuthenticationPrincipal UserEntity userEntity, @RequestBody PurchaseRequest purchaseRequest) {
        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.createPurchase(userEntity, purchaseRequest);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets single purchase by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returned purchase with id{id}",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))}),
            @ApiResponse(responseCode = "403", description = "This purchase does not belong to the specified user"),
            @ApiResponse(responseCode = "419", description = "User with id{userId} not found"),
            @ApiResponse(responseCode = "420", description = "No purchase found with id{id}")})
    @GetMapping("/get/{purchaseId}")
    public ResponseEntity<?> getSingle(@AuthenticationPrincipal UserEntity userEntity, @PathVariable Long purchaseId) {
        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.getSinglePurchase(userEntity, purchaseId);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all purchases")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returned all purchases",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))}),
            @ApiResponse(responseCode = "419", description = "User with id{useId} not found")})

    @GetMapping("/get")
    public ResponseEntity<?> getAll(@AuthenticationPrincipal UserEntity userEntity) {
        Either<PurchaseResponse, List<PurchaseDTO>> result = purchaseService.getAllPurchases(userEntity);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Update purchase by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Updated purchase with id{id}",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))}),
            @ApiResponse(responseCode = "403", description = "This purchase does not belong to the specified user"),
            @ApiResponse(responseCode = "419", description = "User with id{userId} not found"),
            @ApiResponse(responseCode = "420", description = "No purchase found with id{id}")})
    @PutMapping("/delete/{purchaseId}")
    public ResponseEntity<?> update(@AuthenticationPrincipal UserEntity userEntity, @PathVariable Long purchaseId, @RequestBody PurchaseRequest purchaseRequest) {
        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.updatePurchase(userEntity, purchaseId, purchaseRequest);

        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Delete purchase by id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully deleted purchase with id{id}"),
            @ApiResponse(responseCode = "404", description = "No purchase found with id{id}")})
    @DeleteMapping("/delete/{purchaseId}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserEntity userEntity, @PathVariable Long purchaseId) {
        PurchaseResponse result = purchaseService.deletePurchase(userEntity, purchaseId);
        return ResponseEntity.status(result.getCode()).body(result.getMessage());
    }

}
