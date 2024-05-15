package com.develhope.spring.dealershipStatistics.entities;

import lombok.Data;

import java.util.List;

@Data
public class UserStatisticsDTO {
    private int totalOrders;
    private int totalPurchases;
    private int totalRentals;
    private List<OrderDetail> orderDetails;
    private List<PurchaseDetail> purchaseDetails;
    private List<RentalDetail> rentalDetails;
}