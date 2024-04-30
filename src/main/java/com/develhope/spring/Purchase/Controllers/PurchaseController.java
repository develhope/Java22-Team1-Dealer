package com.develhope.spring.Purchase.Controllers;

import com.develhope.spring.Purchase.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Model.PurchaseModel;
import com.develhope.spring.Purchase.Request.PurchaseRequest;
import com.develhope.spring.Purchase.Response.PurchaseResponse;
import com.develhope.spring.Purchase.Service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Gets all purchases")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Returned all purchases",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))}
                    ),
                    @ApiResponse(
                            responseCode ="404",
                            description = "No purchases found"
                    )
            }
    )

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        List<PurchaseDTO> result = purchaseService.getAllPurchases();
        if(result.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @Operation(summary = "Gets single purchase by id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Returned purchase with id{id}",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))}
                    ),
                    @ApiResponse(
                            responseCode ="404",
                            description = "No purchase found with id{id}"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getSingle(@PathVariable Long id) {
        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.getSinglePurchase(id);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Create a purchase with required data")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created purchase",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))}
                    )
            }
    )
    @PostMapping("/")
    public ResponseEntity<PurchaseDTO> create(@RequestBody PurchaseRequest purchaseRequest) {
        return ResponseEntity.ok(purchaseService.createPurchase(purchaseRequest));
    }

    @Operation(summary = "Update purchase by id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated purchase with id{id}",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))}
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No purchase found with id{id}"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PurchaseDTO purchaseDTO) {
        Either<PurchaseResponse, PurchaseDTO> result = purchaseService.updatePurchase(id, PurchaseModel.dtoToModel(purchaseDTO));

        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Delete purchase by id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted purchase with id{id}"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No purchase found with id{id}"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        PurchaseResponse result = purchaseService.deletePurchase(id);
        return ResponseEntity.status(result.getCode()).body(result.getMessage());
    }

}
