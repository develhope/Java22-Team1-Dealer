package com.develhope.spring.dealershipStatistics.service;

import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import com.develhope.spring.Purchase.Repositories.PurchasesLinkRepository;
import com.develhope.spring.Rent.Repositories.RentalsLinkRepository;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.dealershipStatistics.entities.DealershipStatisticsEntity;
import com.develhope.spring.order.Repositories.OrderRepository;
import com.develhope.spring.order.Repositories.OrdersLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealershipStatisticsService {

    @Autowired
    private UserLinkRepository userLinkRepository;

    @Autowired
    private OrdersLinkRepository ordersLinkRepository;

    @Autowired
    private PurchasesLinkRepository purchasesLinkRepository;

    @Autowired
    private RentalsLinkRepository rentalsLinkRepository;

    public DealershipStatisticsEntity getOverallStatistics() {
        DealershipStatisticsEntity dealershipStatistics = new DealershipStatisticsEntity();

        long totalOrders = ordersLinkRepository.count();
        dealershipStatistics.setTotalOrders(totalOrders);

        long totalPurchases = purchasesLinkRepository.count();
        dealershipStatistics.setTotalPurchases(totalPurchases);

        long totalRentals = rentalsLinkRepository.count();
        dealershipStatistics.setTotalRentals(totalRentals);

        long totalUsers = userLinkRepository.count();
        dealershipStatistics.setTotalUsers(totalUsers);

        return dealershipStatistics;
    }

}