package com.develhope.spring.Vehicles.Controllers;

import com.develhope.spring.Vehicles.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Request.VehicleRequest;
import com.develhope.spring.Vehicles.Response.VehicleResponse;
import com.develhope.spring.Vehicles.Services.VehicleService;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    VehicleService vehicleService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> createVehicle(@PathVariable Long id, @RequestBody VehicleRequest vehicleRequest) {
        Either<VehicleResponse, VehicleDTO> result = vehicleService.createVehicle(id, vehicleRequest);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/{userId}/{vehicleId}")
    public ResponseEntity<?> getSingleVehicle(@PathVariable Long userId, @PathVariable Long vehicleId) {
        Either<VehicleResponse, VehicleDTO> result = vehicleService.getSingleVehicle(userId, vehicleId);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result);
        }
    }
}
