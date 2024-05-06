package com.develhope.spring.order.Controllers;

import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.OrderRequest.OrderRequest;
import com.develhope.spring.order.Response.OrderResponse;
import com.develhope.spring.order.Services.OrderService;
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
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Operation(summary = "Create a order with required data")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "Successfully created order",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Deposit is negative"),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})

    @PostMapping("/{userId}")
    public ResponseEntity<?> create(@PathVariable Long buyerId,@RequestParam(required = false) Long intermediaryId, @RequestBody OrderRequest orderRequest) {
        Either<OrderResponse, OrderDTO> result = orderService.create(buyerId, intermediaryId, orderRequest);

        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @Operation(summary = "Gets user's order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found order",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Order does not belong to specified user"),
            @ApiResponse(responseCode = "404", description = "Specified order not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})
    @GetMapping("/{userId}/{orderId}")
    public ResponseEntity<?> getSingle(@PathVariable Long userId, @PathVariable Long orderId) {
        Either<OrderResponse, OrderDTO> result = orderService.getSingle(userId, orderId);
        return createResponseEntity(result);
    }

    @Operation(summary = "Gets all user's orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully found orders",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "404", description = "No orders found for specified user")})
    @GetMapping("/{userId}")
    public ResponseEntity<?> getAll(@PathVariable Long userId) {
        Either<OrderResponse, List<OrderDTO>> result = orderService.getAll(userId);

        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }

    }

    @Operation(summary = "Updates user's specified order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully modified order",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Order does not belong to specified user"),
            @ApiResponse(responseCode = "404", description = "Specified order not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})
    @PutMapping("/{userId}/{orderId}")
    public ResponseEntity<?> update(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody OrderRequest orderRequest) {
        Either<OrderResponse, OrderDTO> result = orderService.update(userId, orderId,  orderRequest);
        return createResponseEntity(result);
    }

    @Operation(summary = "Admin updates user's specified order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully modified order",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = OrderDTO.class))}),
            @ApiResponse(responseCode = "403", description = "Order does not belong to specified user"),
            @ApiResponse(responseCode = "403", description = "Specified user is not an admin"),
            @ApiResponse(responseCode = "404", description = "Specified order not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found")})
    @PutMapping("/admin/{userId}/{orderId}")
    public ResponseEntity<?> updateByAdmin(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody OrderRequest orderRequest) {
        Either<OrderResponse, OrderDTO> result = orderService.update(userId, orderId, orderRequest);
        return createResponseEntity(result);
    }

    @Operation(summary = "Deletes user's specified order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted order"),
            @ApiResponse(responseCode = "403", description = "Order does not belong to specified user"),
            @ApiResponse(responseCode = "404", description = "Specified order not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @DeleteMapping("/{userId}/{orderId}")
    public ResponseEntity<?> delete(@PathVariable Long userId, @PathVariable Long orderId) {
        OrderResponse result = orderService.deleteOrder(userId, orderId);
        return ResponseEntity.status(result.getCode()).body(result.getMessage());
    }

    @Operation(summary = "Admin deletes user's specified order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted order"),
            @ApiResponse(responseCode = "403", description = "Order does not belong to specified user"),
            @ApiResponse(responseCode = "403", description = "Specified user is not an admin"),
            @ApiResponse(responseCode = "404", description = "Specified order not found"),
            @ApiResponse(responseCode = "404", description = "Specified user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    @DeleteMapping("/admin/{userId}/{orderId}")
    public ResponseEntity<?> deleteByAdmin(@PathVariable Long userId, @PathVariable Long orderId) {
        OrderResponse result = orderService.deleteOrder(userId, orderId);
        return ResponseEntity.status(result.getCode()).body(result.getMessage());
    }

    private ResponseEntity<?> createResponseEntity(Either<OrderResponse, OrderDTO> result) {
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

}
