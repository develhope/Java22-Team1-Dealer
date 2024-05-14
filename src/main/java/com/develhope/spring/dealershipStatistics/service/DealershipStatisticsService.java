package com.develhope.spring.dealershipStatistics.service;

import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import com.develhope.spring.Rent.Repositories.RentalsLinkRepository;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.dealershipStatistics.entities.DealershipStatisticsEntity;
import com.develhope.spring.order.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealershipStatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private RentalsLinkRepository rentalRepository;

    public DealershipStatisticsEntity getOverallStatistics() {
        DealershipStatisticsEntity dealershipStatistics = new DealershipStatisticsEntity();

        long totalOrders = orderRepository.count();
        dealershipStatistics.setTotalOrders(totalOrders);

        long totalPurchases = purchaseRepository.count();
        dealershipStatistics.setTotalPurchases(totalPurchases);

        long totalRentals = rentalRepository.count();
        dealershipStatistics.setTotalRentals(totalRentals);

        long totalUsers = userRepository.count();
        dealershipStatistics.setTotalUsers(totalUsers);

        return dealershipStatistics;
    }

}