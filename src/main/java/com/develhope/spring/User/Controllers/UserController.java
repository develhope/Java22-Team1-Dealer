package com.develhope.spring.User.Controllers;


import com.develhope.spring.User.Entities.UserDTO;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Response.UserResponse;
import com.develhope.spring.User.Services.UserService;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.Vehicles.Response.VehicleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<VehicleEntity> getVehicleById(@PathVariable(name = "id") Long id) {
       return ResponseEntity.ok().body(vehicleRepository.getReferenceById(id));
    }

    @Operation(summary = "Gets single user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found user",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})
    @GetMapping("/getSingle/{userId}")
    public ResponseEntity<?> getSingleUser(@AuthenticationPrincipal UserEntity userEntity, @PathVariable Long userId) {
        Either<UserResponse, UserDTO> result = userService.getSingleUser(userEntity, userId);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result.get());
        }
    }


}