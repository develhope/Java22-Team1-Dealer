package com.develhope.spring.Vehicles.Controllers;

import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import com.develhope.spring.Vehicles.Request.VehicleRequest;
import com.develhope.spring.Vehicles.Response.VehicleResponse;
import com.develhope.spring.Vehicles.Services.VehicleResearchService;
import com.develhope.spring.Vehicles.Services.VehicleCRUDService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    VehicleCRUDService vehicleCRUDService;

    @Autowired
    VehicleResearchService vehicleResearchService;

    @Operation(summary = "Create a vehicle")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successfully created vehicle",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})

    @PostMapping("/{userId}")
    public ResponseEntity<?> createVehicle(@PathVariable Long id, @RequestBody VehicleRequest vehicleRequest) {
        Either<VehicleResponse, VehicleDTO> result = vehicleCRUDService.createVehicle(id, vehicleRequest);
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
        Either<VehicleResponse, VehicleDTO> result = vehicleCRUDService.getSingleVehicle(userId, vehicleId);
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
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleCRUDService.getAllVehicle(userId);
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
        Either<VehicleResponse, VehicleDTO> result = vehicleCRUDService.updateVehicle(userId, vehicleId, vehicleRequest);
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
        VehicleResponse result = vehicleCRUDService.deleteVehicle(userId, vehicleId);
        return ResponseEntity.status(result.getCode()).body(result.getMessage());
    }

    @Operation(summary = "Gets all vehicles by color")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/color/{userId}")
    public ResponseEntity<?> findByColor(@PathVariable Long userId, @RequestParam String color) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByColor(userId, color);
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

    @GetMapping("/model/{userId}")
    public ResponseEntity<?> findByModel(@PathVariable Long userId, @RequestParam String model) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByModel(userId, model);
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

    @GetMapping("/brand/{userId}")
    public ResponseEntity<?> findByBrand(@PathVariable Long userId, @RequestParam String brand) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByBrand(userId, brand);
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

    @GetMapping("/transmission/{userId}")
    public ResponseEntity<?> findByTransmission(@PathVariable Long userId, @RequestParam String transmission) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByTransmission(userId, transmission);
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

    @GetMapping("/powerSupply/{userId}")
    public ResponseEntity<?> findByPowerSupply(@PathVariable Long userId, @RequestParam String powerSupply) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByPowerSupply(userId, powerSupply);
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

    @GetMapping("/accessories/{userId}")
    public ResponseEntity<?> findByAccessories(@PathVariable Long userId, @RequestBody List<String> accessories) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByAccessories(userId, accessories);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by displacement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "400", description = "the minimum displacement cannot be higher than the maximum"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/displacement/{userId}")
    public ResponseEntity<?> findByDisplacement(@PathVariable Long userId, @RequestParam Integer min, @RequestParam Integer max) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByDisplacement(userId, min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by power")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "400", description = "the minimum power cannot be higher than the maximum"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/power/{userId}")
    public ResponseEntity<?> findByPower(@PathVariable Long userId, @RequestParam Integer min, @RequestParam Integer max) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByPower(userId, min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by registration year")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "400", description = "the minimum registration year cannot be higher than the maximum"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/registrationYear/{userId}")
    public ResponseEntity<?> findByRegistrationYear(@PathVariable Long userId, @RequestParam Integer min, @RequestParam Integer max) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByRegistrationYear(userId, min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "400", description = "the minimum price cannot be higher than the maximum"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/price/{userId}")
    public ResponseEntity<?> findByPrice(@PathVariable Long userId, @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByPrice(userId, min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by discount price")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "400", description = "the minimum discount price cannot be higher than the maximum"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/discount/{userId}")
    public ResponseEntity<?> findByDiscount(@PathVariable Long userId, @RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByDiscount(userId, min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by isNew")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/isNew/{userId}")
    public ResponseEntity<?> findByIsNew(@PathVariable Long userId, @RequestParam boolean isNew) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByIsNew(userId, isNew);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/vehicleStatus/{userId}")
    public ResponseEntity<?> findByVehicleStatus(@PathVariable Long userId, @RequestParam VehicleStatus vehicleStatus) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleStatus(userId, vehicleStatus);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all vehicles by type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/vehicleType/{userId}")
    public ResponseEntity<?> findByVehicleType(@PathVariable Long userId, @RequestParam VehicleType vehicleType) {
        Either<VehicleResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleType(userId, vehicleType);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

}
