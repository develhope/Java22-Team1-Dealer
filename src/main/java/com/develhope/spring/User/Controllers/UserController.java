package com.develhope.spring.User.Controllers;


import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserDTO;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Request.UserRequest;
import com.develhope.spring.User.Response.UserResponse;
import com.develhope.spring.User.Services.UserService;
import com.develhope.spring.Vehicles.Entities.DTO.VehicleDTO;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.Vehicles.Request.VehicleRequest;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;



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

    @Operation(summary = "Gets all users by type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found user",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})
    @GetMapping("/findBy/userType")
    public ResponseEntity<?> findByUserType(@RequestParam String userType) {
        Either<UserResponse, List<UserDTO>> result = userService.findByUserType(userType);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets all users by name and surname")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found user",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})
    @GetMapping("/findBy/nameAndSurname")
    public ResponseEntity<?> findByNameAndSurname(@RequestParam String name, @RequestParam String surname) {
        Either<UserResponse, UserDTO> result = userService.findByNameAndSurname(name, surname);
        if (result.isRight()) {
            return ResponseEntity.ok(result.get());
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Updates a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully modified user",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = VehicleDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})

    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@AuthenticationPrincipal UserEntity userEntity, @PathVariable Long userId, @RequestBody UserRequest userRequest) {
        Either<UserResponse, UserDTO> result = userService.updateUser(userEntity, userId, userRequest);
        if (result.isLeft()) {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        } else {
            return ResponseEntity.ok(result.get());
        }
    }

    @Operation(summary = "Deletes a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserEntity userEntity, @PathVariable Long userId) {
        UserResponse result = userService.deleteUser(userEntity, userId);
        return ResponseEntity.status(result.getCode()).body(result.getMessage());
    }
}