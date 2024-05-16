package com.develhope.spring.dealershipStatistics.entities;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Rent.Entities.DTO.RentDTO;
import com.develhope.spring.order.DTO.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDTO {
    private int totalOrders;
    private int totalPurchases;
    private int totalRentals;
    private List<OrderDTO> orderDetails;
    private List<PurchaseDTO> purchaseDetails;
    private List<RentDTO> rentalDetails;
}