package com.develhope.spring.dealershipStatistics.service;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseModel;
import com.develhope.spring.Purchase.Entities.PurchasesLinkEntity;
import com.develhope.spring.Purchase.Repositories.PurchasesLinkRepository;
import com.develhope.spring.Rent.Entities.DTO.RentModel;
import com.develhope.spring.Rent.Entities.RentLink;
import com.develhope.spring.Rent.Repositories.RentalsLinkRepository;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.dealershipStatistics.entities.StatisticsDTO;
import com.develhope.spring.order.Entities.OrderEntity;
import com.develhope.spring.order.Entities.OrdersLinkEntity;
import com.develhope.spring.order.Model.OrderModel;
import com.develhope.spring.order.Repositories.OrdersLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class DealershipStatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrdersLinkRepository ordersLinkRepository;

    @Autowired
    private PurchasesLinkRepository purchasesLinkRepository;

    @Autowired
    private RentalsLinkRepository rentalsLinkRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public Integer getRentsNumberOfUser(UserEntity user, Long targetId) {
        if (user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if (userOptional.isEmpty()) {
                return null;
            }
            List<RentLink> rentLinkList = rentalsLinkRepository.findAllBySeller_Id(targetId);
            return rentLinkList.size();
        } else {
            List<RentLink> rentLinkList = rentalsLinkRepository.findAllByBuyer_Id(user.getId());
            return rentLinkList.size();
        }
    }

    public Integer getPurchasesNumberOfUser(UserEntity user, Long targetId) {
        if (user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if (userOptional.isEmpty()) {
                return null;
            }
            List<PurchasesLinkEntity> purchasesList = purchasesLinkRepository.findAllBySeller_Id(targetId); //TODO fixare nel repository la query
            return purchasesList.size();
        } else {
            List<PurchasesLinkEntity> purchasesList = purchasesLinkRepository.findByBuyer_Id(user.getId()); //TODO fixare nel repository la query
            return purchasesList.size();
        }
    }

    public Integer getOrdersNumberOfUser(UserEntity user, Long targetId) {
        if (user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if (userOptional.isEmpty()) {
                return null;
            }
            List<OrdersLinkEntity> ordersList = ordersLinkRepository.findAllBySeller_Id(targetId); //TODO fixare nel repository la query
            return ordersList.size();
        } else {
            List<OrdersLinkEntity> ordersList = ordersLinkRepository.findAllByBuyer_Id(user.getId()); //TODO fixare nel repository la query
            return ordersList.size();
        }
    }

    public StatisticsDTO getTotalDelearshipStatistics(UserEntity user) {

        List<OrdersLinkEntity> orderList = ordersLinkRepository.findAll();
        List<RentLink> rentLinkList = rentalsLinkRepository.findAll();
        List<PurchasesLinkEntity> purchasesLinkList = purchasesLinkRepository.findAll();
        return new StatisticsDTO(orderList.size(), purchasesLinkList.size(), rentLinkList.size(), orderList.stream().map(orderLink -> OrderModel.modelToDto(OrderModel.entityToModel(orderLink.getOrder()))).toList(), purchasesLinkList.stream().map(purchasesLink -> PurchaseModel.modelToDto(PurchaseModel.entityToModel(purchasesLink.getPurchase()))).toList(), rentLinkList.stream().map(rentLink -> RentModel.modelToDTO(RentModel.entityToModel(rentLink.getRent()))).toList());
    }

    public Map<VehicleType, Integer> getVehicleCountByType() {
        List<VehicleEntity> vehicles = vehicleRepository.findAll();
        Map<VehicleType, Integer> vehicleCountByType = new HashMap<>();

        for (VehicleEntity vehicle : vehicles) {
            VehicleType vehicleType = vehicle.getVehicleType();
            vehicleCountByType.put(vehicleType, vehicleCountByType.getOrDefault(vehicleType, 0) + 1);
        }

        return vehicleCountByType;
    }

    public double getCustomerReturnFrequency() {
    }

    public Map<String, Object> getUserStatistics(UserEntity user) {
        Map<String, Object> statistics = new HashMap<>();

        List<OrdersLinkEntity> orders = ordersLinkRepository.findAllByBuyer_Id(user.getId());
        statistics.put("orders", orders.size());

        List<PurchasesLinkEntity> purchases = purchasesLinkRepository.findByBuyer_Id(user.getId());
        statistics.put("acquisti", purchases.size());

        List<RentLink> rentals = rentalsLinkRepository.findAllByBuyer_Id(user.getId());
        statistics.put("rentals", rentals.size());

        return statistics;
    }

    public Map<String, Object> getFinancialStatistics() {
        //TODO implementare il controllo che sara solo ADMIN

        BigDecimal totalRentals = rentalsLinkRepository.findAll().stream()
                .map(rentLink -> rentLink.getRent().getTotalCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPurchases = purchasesLinkRepository.findAll().stream()
                .map(purchaseLink -> purchaseLink.getPurchase().getVehicle().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOrders = ordersLinkRepository.findAll().stream()
                .filter(orderLink -> orderLink.getOrder().getIsPaid())
                .map(orderLink -> orderLink.getOrder().getVehicle().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRevenue = totalRentals.add(totalPurchases).add(totalOrders);

        Map<String, Object> financialStatistics = new HashMap<>();
        financialStatistics.put("rentals", totalRentals + "€");
        financialStatistics.put("orders", totalOrders + "€");
        financialStatistics.put("purchases", totalPurchases + "€");
        financialStatistics.put("total", totalRevenue + "€");

        return financialStatistics;
    }

    public Map<String, Map<String, Integer>> getVehicleCountByBrandAndModel() {
        List<Object[]> results = vehicleRepository.countVehiclesByBrandAndModel();
        Map<String, Map<String, Integer>> vehicleCountByBrandAndModel = new HashMap<>();

        for (Object[] result : results) {
            String brand = (String) result[0];
            String model = (String) result[1];
            Integer count = ((Long) result[2]).intValue();

            Map<String, Integer> modelCount = vehicleCountByBrandAndModel.getOrDefault(brand, new HashMap<>());
            modelCount.put(model, count);
            vehicleCountByBrandAndModel.put(brand, modelCount);
        }

        return vehicleCountByBrandAndModel;
    }

    public Map<String, Object> getStatisticsByTimePeriod(String timePeriod) {
        // TODO Implementazione per recuperare le statistiche per periodo di tempo dal database
    }

    public Map<UserEntity, Integer> getSellerSalesByTimePeriod(UserEntity user, Long sellerId, LocalDate startDate, LocalDate endDate) {
        if (user.getUserType() == UserTypes.ADMIN) {
            List<PurchasesLinkEntity> sellerSales = purchasesLinkRepository.findAllBySellerIdInBetweenDates(sellerId, startDate, endDate);
            return Map.of(sellerSales.getFirst().getSeller(), sellerSales.size());
        } else {
            return null;
        }
    }

    public Map<UserEntity, BigDecimal> getSellerRevenueByTimePeriod(UserEntity user, Long sellerId, LocalDate startDate, LocalDate endDate) {
        if (user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> foundSeller = userRepository.findById(sellerId);
            if (foundSeller.isEmpty() || foundSeller.get().getUserType() != UserTypes.SELLER) {
                return null;
            }
            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal sellerSalesRevenue = purchasesLinkRepository.findAllBySellerIdInBetweenDates(sellerId, startDate, endDate).stream()
                    .map(sale -> sale.getPurchase().getVehicle().getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            sum = sum.add(sellerSalesRevenue);

            BigDecimal sellerRentsRevenue = rentalsLinkRepository.findAllBySellerIdBetweenDates(sellerId, startDate, endDate).stream().map(rentLink ->
                    rentLink.getRent().getTotalCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
            sum = sum.add(sellerRentsRevenue);

            BigDecimal sellerOrdersRevenue = ordersLinkRepository.findAllBySelleridBetweenDates(sellerId, startDate, endDate).stream().map(
                    ordersLink -> ordersLink.getOrder().getIsPaid() ? ordersLink.getOrder().getVehicle().getPrice() : ordersLink.getOrder().getDeposit()
            ).reduce(BigDecimal.ZERO, BigDecimal::add);
            sum = sum.add(sellerOrdersRevenue);


            return Map.of(
                    foundSeller.get(),
                    sum
            );
        } else {
            return null;
        }
    }

    public BigDecimal getDealershipRevenueByTimePeriod(UserEntity user,LocalDate startDate, LocalDate endDate) {
        if (user.getUserType() == UserTypes.ADMIN) {
            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal sellerSalesRevenue = purchasesLinkRepository.findAllInBetweenDates(startDate, endDate).stream()
                    .map(sale -> sale.getPurchase().getVehicle().getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            sum = sum.add(sellerSalesRevenue);

            BigDecimal sellerRentsRevenue = rentalsLinkRepository.findAllBetweenDates(startDate, endDate).stream().map(rentLink ->
                    rentLink.getRent().getTotalCost()).reduce(BigDecimal.ZERO, BigDecimal::add);
            sum = sum.add(sellerRentsRevenue);

            BigDecimal sellerOrdersRevenue = ordersLinkRepository.findAllBetweenDates(startDate, endDate).stream().map(
                    ordersLink -> ordersLink.getOrder().getIsPaid() ? ordersLink.getOrder().getVehicle().getPrice() : ordersLink.getOrder().getDeposit()
            ).reduce(BigDecimal.ZERO, BigDecimal::add);
            sum = sum.add(sellerOrdersRevenue);

            return sum;
        } else {
            return null;
        }
    }


    public double getCustomerReturnFrequency() {
    }

    public Map<String, Object> getDealershipStatistics() {
        // TODO Implementazione per recuperare le statistiche complessive del concessionario dal database
    }

    public Map<String, Object> getUserStatistics(UserEntity user) {
        // TODO Implementazione per recuperare le statistiche complessive per un singolo utente dal database
    }

    public Map<String, Object> getFinancialStatistics() {
        // TODO Implementazione per recuperare le statistiche monetarie dal database
    }

    public Map<String, Integer> getVehicleCountByBrandOrModel() {
        // TODO Implementazione per recuperare il numero di veicoli per marca o modello dal database
    }

    public Map<String, Object> getStatisticsByTimePeriod(String timePeriod) {
        // TODO Implementazione per recuperare le statistiche per periodo di tempo dal database
    }
}