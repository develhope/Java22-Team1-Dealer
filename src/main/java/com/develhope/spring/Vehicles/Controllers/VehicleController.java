package com.develhope.spring.Vehicles.Controllers;

import com.develhope.spring.Vehicles.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Request.VehicleRequest;
import com.develhope.spring.Vehicles.Response.VehicleResponse;
import com.develhope.spring.Vehicles.Services.VehicleService;
import com.develhope.spring.order.DTO.OrderDTO;
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
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    VehicleService vehicleService;

    @Operation(summary = "Create a vehicle")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successfully created vehicle",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})

    @PostMapping("/{userId}")
    public ResponseEntity<?> createVehicle(@PathVariable Long id, @RequestBody VehicleRequest vehicleRequest) {
        Either<VehicleResponse, VehicleDTO> result = vehicleService.createVehicle(id, vehicleRequest);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @Operation(summary = "Gets single vehicle by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicle",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})
    @GetMapping("/{userId}/{vehicleId}")
    public ResponseEntity<?> getSingleVehicle(@PathVariable Long userId, @PathVariable Long vehicleId) {
        Either<VehicleResponse, VehicleDTO> result = vehicleService.getSingleVehicle(userId, vehicleId);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @Operation(summary = "Gets all vehicles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAll(@PathVariable Long userId) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleService.getAllVehicle(userId);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Updates a vehicle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully modified vehicle",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})

    @PutMapping("/{userId}/{vehicleId}")
    public ResponseEntity<?> updateVehicle(@PathVariable Long userId, @PathVariable Long vehicleId, @RequestBody VehicleRequest vehicleRequest) {
        Either<VehicleResponse, VehicleDTO> result = vehicleService.updateVehicle(userId, vehicleId, vehicleRequest);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @Operation(summary = "Deletes a vehicle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted vehicle"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})

    @DeleteMapping("/{userId}/{vehicleId}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long userId, @PathVariable Long vehicleId) {
        VehicleResponse result = vehicleService.deleteVehicle(userId, vehicleId);
        return ResponseEntity.status(result.getCode()).body(result.getMessage());
    }

    @Operation(summary = "Gets all vehicles by color")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/{userId}")
    public ResponseEntity<?> findByColor(@PathVariable Long userId, @RequestParam String color) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleService.findByColor(userId, color);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by model")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/{userId}")
    public ResponseEntity<?> findByModel(@PathVariable Long userId, @RequestParam String model) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleService.findByModel(userId, model);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by brand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/{userId}")
    public ResponseEntity<?> findByBrand(@PathVariable Long userId, @RequestParam String brand) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleService.findByBrand(userId, brand);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by transmission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/{userId}")
    public ResponseEntity<?> findByTransmission(@PathVariable Long userId, @RequestParam String transmission) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleService.findByTransmission(userId, transmission);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by power supply")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/{userId}")
    public ResponseEntity<?> findByPowerSupply(@PathVariable Long userId, @RequestParam String powerSupply) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleService.findByPowerSupply(userId, powerSupply);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by accessories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/{userId}")
    public ResponseEntity<?> findByAccessories(@PathVariable Long userId, @RequestBody List<String> accessories) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleService.findByAccessories(userId, accessories);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }


}
