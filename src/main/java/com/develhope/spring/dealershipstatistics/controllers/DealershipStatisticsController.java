package com.develhope.spring.dealershipstatistics.controllers;

import com.develhope.spring.dealershipstatistics.entities.StatisticsDTO;
import com.develhope.spring.dealershipstatistics.service.DealershipStatisticsService;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class DealershipStatisticsController {
    @Autowired
    private DealershipStatisticsService dealershipStatisticsService;

    @GetMapping("/getNumberOfRents")
    public ResponseEntity<?> getUserNumberOfRents(@AuthenticationPrincipal UserEntity user, @RequestParam(required = false) Long targetId) {
        return dealershipStatisticsService.getRentsNumberOfUser(user, targetId);
    }

    @GetMapping("/getNumberOfPurchase")
    public ResponseEntity<?> getUserNumberOfPurchase(@AuthenticationPrincipal UserEntity user, @RequestParam(required = false) Long targetId) {
        return dealershipStatisticsService.getPurchasesNumberOfUser(user, targetId);
    }

    @GetMapping("/getNumberOfOrders")
    public ResponseEntity<?> getUserNumberOfOrders(@AuthenticationPrincipal UserEntity user, @RequestParam(required = false) Long targetId) {
        return dealershipStatisticsService.getOrdersNumberOfUser(user, targetId);
    }

    @GetMapping("/getAllDealershipStatistics")
    public ResponseEntity<?> getAllDealershipStatistics(@AuthenticationPrincipal UserEntity user) {
        StatisticsDTO statisticsDTO = dealershipStatisticsService.getAllDelearshipStatistics(user);
        return statisticsDTO == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(statisticsDTO);
    }

    @GetMapping("/getVehicleCountByType")
    public ResponseEntity<?> getVehicleCountByType(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(dealershipStatisticsService.getVehicleCountByType());
    }

    @GetMapping("/admin/getSellerSaleByPeriod/{sellerId}")
    public ResponseEntity<?> getSellerSaleByPeriod(@AuthenticationPrincipal UserEntity user, @PathVariable Long sellerId, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        Map<UserEntity, Integer> sellerStats = dealershipStatisticsService.getSellerSalesByTimePeriod(user, sellerId, startDate, endDate);
        return sellerStats == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(sellerStats);
    }

    @GetMapping("/admin/getSellerRevenueByTimePeriod/{sellerId}")
    public ResponseEntity<?> getSellerRevenueByTimePeriod(@AuthenticationPrincipal UserEntity user, @PathVariable Long sellerId, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        Map<UserEntity, BigDecimal> sellerStats = dealershipStatisticsService.getSellerRevenueByTimePeriod(user, sellerId, startDate, endDate);
        return sellerStats == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(sellerStats);
    }

    @GetMapping("/admin/getDealerShipRevenueByTimePeriod")
    public ResponseEntity<?> getDealerShipRevenueByTimePeriod(@AuthenticationPrincipal UserEntity user, @RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        BigDecimal result = dealershipStatisticsService.getDealershipRevenueByTimePeriod(user, startDate, endDate);
        return result == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(result);
    }

    @GetMapping("/most-sold-vehicles")
    public Map<String, Long> getMostSoldVehicles() {
        return dealershipStatisticsService.getMostSoldVehicles();
    }

    @GetMapping("/highest-sale-price-vehicle")
    public VehicleEntity getHighestSalePriceVehicle() {
        return dealershipStatisticsService.getHighestSalePriceVehicle();
    }

    @GetMapping("/most-searched-vehicles")
    public Map<String, Integer> getMostSearchedVehicles() {
        return dealershipStatisticsService.getMostSearchedVehicles();
    }

    @GetMapping("/most-ordered-vehicles")
    public Map<String, Long> getMostOrderedVehicles() {
        return dealershipStatisticsService.getMostOrderedVehicles();
    }
}
