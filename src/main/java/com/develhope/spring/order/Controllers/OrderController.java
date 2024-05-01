package com.develhope.spring.order.Controllers;

import com.develhope.spring.Purchase.DTO.PurchaseDTO;
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

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Operation(summary = "Create a purchase with required data")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created purchase",
                            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PurchaseDTO.class))}
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Deposit is negative"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User with id{userId} not found"
                    )
            }
    )
    @PostMapping("/admin/{adminId}/{userId}")
    public ResponseEntity<?> create(@PathVariable Long adminId, @PathVariable Long userId, @RequestBody OrderRequest orderRequest) {
        Either<OrderResponse, OrderDTO> result = orderService.createByAdmin(adminId, userId, orderRequest);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @PostMapping("/{userId}")
    public ResponseEntity<?> create(@PathVariable Long userId, @RequestBody OrderRequest orderRequest) {
        Either<OrderResponse, OrderDTO> result = orderService.create(userId, orderRequest);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
    }

    @GetMapping("/{userId}/{orderId}")
    public ResponseEntity<?> getSingle(@PathVariable Long userId, @PathVariable Long orderId) {
        Either<OrderResponse, OrderDTO> result = orderService.getSingle(userId, orderId);
        if (result.isRight()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
        }
   }

   @PostMapping("/{userId}/{orderId}")
    public ResponseEntity<?> update(@PathVariable Long userId, @PathVariable Long orderId, @RequestBody OrderRequest orderRequest) {
       Either<OrderResponse, OrderDTO> result = orderService.update(userId, orderId, orderRequest);
       if (result.isRight()) {
           return ResponseEntity.ok(result);
       } else {
           return ResponseEntity.status(result.getLeft().getCode()).body(result.getLeft().getMessage());
       }
   }
}
