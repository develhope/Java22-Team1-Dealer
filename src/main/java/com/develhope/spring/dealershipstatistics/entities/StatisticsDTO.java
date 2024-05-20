package com.develhope.spring.dealershipstatistics.entities;

import com.develhope.spring.purchase.DTO.PurchaseDTO;
import com.develhope.spring.rent.DTO.RentDTO;
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