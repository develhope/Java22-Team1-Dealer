package com.develhope.spring.User.Controllers.ControllerSeller;


import com.develhope.spring.Vehicles.Entities.Vehicle;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sellers")
public class ControllerSeller {
    @Autowired
    VehicleRepository vehicleRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable(name = "id") Long id) {
       return ResponseEntity.ok().body(vehicleRepository.getReferenceById(id));
    }
}