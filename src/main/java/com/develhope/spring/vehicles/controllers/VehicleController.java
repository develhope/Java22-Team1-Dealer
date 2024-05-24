package com.develhope.spring.vehicles.controllers;

import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.vehicles.DTO.VehicleDTO;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.entities.VehicleType;
import com.develhope.spring.vehicles.request.VehicleRequest;
import com.develhope.spring.vehicles.response.VehicleErrorResponse;
import com.develhope.spring.vehicles.services.VehicleResearchService;
import com.develhope.spring.vehicles.services.VehicleCRUDService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    VehicleCRUDService vehicleCRUDService;

    @Autowired
    VehicleResearchService vehicleResearchService;

    @GetMapping("/checkUser")
    public UserTypes controlloUser(@AuthenticationPrincipal UserEntity userEntity) {
        if (userEntity.getUserType() == UserTypes.ADMIN) {
            return userEntity.getUserType();
        }
        return null;
    }

    @Operation(summary = "Create a vehicle")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successfully created vehicle",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})

    @PostMapping("/create")
    public ResponseEntity<?> createVehicle(@AuthenticationPrincipal UserEntity userEntity, @RequestBody VehicleRequest vehicleRequest) {
        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.createVehicle(userEntity, vehicleRequest);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result.get());
        }
    }

    @Operation(summary = "Gets single vehicle by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicle",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})
    @GetMapping("/getSingle/{vehicleId}")
    public ResponseEntity<?> getSingleVehicle(@AuthenticationPrincipal UserEntity userEntity, @PathVariable Long vehicleId) {
        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.getSingleVehicle(userEntity, vehicleId);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result.get());
        }
    }

    @Operation(summary = "Gets all vehicles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll(@AuthenticationPrincipal UserEntity userEntity) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleCRUDService.getAllVehicle();
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @PutMapping("/update/{vehicleId}")
    public ResponseEntity<?> updateVehicle(@AuthenticationPrincipal UserEntity userEntity, @PathVariable Long vehicleId, @RequestBody VehicleRequest vehicleRequest) {
        Either<VehicleErrorResponse, VehicleDTO> result = vehicleCRUDService.updateVehicle(userEntity, vehicleId, vehicleRequest);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result.get());
        }
    }

    @Operation(summary = "Deletes a vehicle")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted vehicle"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})

    @DeleteMapping("/delete/{vehicleId}")
    public ResponseEntity<?> deleteVehicle(@AuthenticationPrincipal UserEntity userEntity, @PathVariable Long vehicleId) {
        VehicleErrorResponse result = vehicleCRUDService.deleteVehicle(userEntity, vehicleId);
        return ResponseEntity.status(result.getCode()).body(result.getMessage());
    }

    @Operation(summary = "Gets all vehicles by color")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found vehicles",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "Specified vehicle not found")})

    @GetMapping("/findBy/color")
    public ResponseEntity<?> findByColor(@RequestParam String color) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByColor(color);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/model")
    public ResponseEntity<?> findByModel(@RequestParam String model) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByModel(model);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/brand")
    public ResponseEntity<?> findByBrand(@RequestParam String brand) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByBrand(brand);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/transmission")
    public ResponseEntity<?> findByTransmission(@RequestParam String transmission) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByTransmission(transmission);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/powerSupply")
    public ResponseEntity<?> findByPowerSupply(@RequestParam String powerSupply) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPowerSupply(powerSupply);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/accessories")
    public ResponseEntity<?> findByAccessories(@RequestBody List<String> accessories) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByAccessories(accessories);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/displacement")
    public ResponseEntity<?> findByDisplacement(@RequestParam Integer min, @RequestParam Integer max) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDisplacement(min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/power")
    public ResponseEntity<?> findByPower(@RequestParam Integer min, @RequestParam Integer max) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPower(min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/registrationYear")
    public ResponseEntity<?> findByRegistrationYear(@RequestParam Integer min, @RequestParam Integer max) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByRegistrationYear(min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/price")
    public ResponseEntity<?> findByPrice(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByPrice(min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/discount")
    public ResponseEntity<?> findByDiscount(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByDiscount(min, max);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/isNew")
    public ResponseEntity<?> findByIsNew(@RequestParam boolean isNew) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByIsNew(isNew);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/vehicleStatus")
    public ResponseEntity<?> findByVehicleStatus(@RequestParam VehicleStatus vehicleStatus) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleStatus(vehicleStatus);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
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

    @GetMapping("/findBy/vehicleType")
    public ResponseEntity<?> findByVehicleType(@RequestParam VehicleType vehicleType) {
        Either<VehicleErrorResponse, List<VehicleDTO>> result = vehicleResearchService.findByVehicleType(vehicleType);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

}