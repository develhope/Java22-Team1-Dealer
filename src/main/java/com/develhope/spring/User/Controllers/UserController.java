package com.develhope.spring.User.Controllers.ControllerSeller;


import com.develhope.spring.User.Services.UserService;
import com.develhope.spring.User.UserDTO.UserDTO;
import com.develhope.spring.User.UserDTO.UserModel;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.OrderRequest.OrderRequest;
import com.develhope.spring.order.Response.OrderResponse;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sellers")
public class UserController {
    @Autowired
    VehicleRepository vehicleRepository;
    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<VehicleEntity> getVehicleById(@PathVariable(name = "id") Long id) {
       return ResponseEntity.ok().body(vehicleRepository.getReferenceById(id));
    }
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody UserDTO createUserRequest) {
        UserDTO result = userService.createUser(createUserRequest);
        if (result == null) {
            return ResponseEntity.status(420).body("nain");
        } else {
            return ResponseEntity.ok(result);
        }
    }
    @DeleteMapping("/delete/user/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable long userId) {
        boolean result = userService.deleteUser(userId);
        if (result) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(421).body("impossible to delete the user");
        }
    }
    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getSingleUser(@PathVariable long userId) {
        UserDTO result = userService.getSingleUser(userId);
        if (result == null) {
            return ResponseEntity.status(422).body("user not found");
        } else {
            return ResponseEntity.ok(result);
        }
    }
    @PatchMapping("/update/user/{userId}")
    public ResponseEntity<?> updateSingleUser(@PathVariable long userId, @RequestBody UserModel updateUserRequest) {
        UserDTO result = userService.updateUser(userId, updateUserRequest);
        if (result == null) {
            return ResponseEntity.status(422).body("user not found");
        } else {
            return ResponseEntity.ok(result);
        }
    }
}