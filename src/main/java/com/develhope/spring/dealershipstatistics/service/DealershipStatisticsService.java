package com.develhope.spring.dealershipstatistics.service;

import com.develhope.spring.purchase.model.PurchaseModel;
import com.develhope.spring.purchase.entities.PurchasesLinkEntity;
import com.develhope.spring.purchase.repositories.PurchaseRepository;
import com.develhope.spring.purchase.repositories.PurchasesLinkRepository;
import com.develhope.spring.rent.model.RentModel;
import com.develhope.spring.rent.entities.RentLink;
import com.develhope.spring.rent.repositories.RentRepository;
import com.develhope.spring.rent.repositories.RentalsLinkRepository;
import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleType;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import com.develhope.spring.dealershipstatistics.entities.StatisticsDTO;
import com.develhope.spring.order.entities.OrdersLinkEntity;
import com.develhope.spring.order.model.OrderModel;
import com.develhope.spring.order.repositories.OrderRepository;
import com.develhope.spring.order.repositories.OrdersLinkRepository;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RentRepository rentRepository;

    // TODO VEICOLO PIU VENDUTO, VENDUTO PIU ALTO, PIU RICERCATO ,ORDINATO

    public ResponseEntity<?> getRentsNumberOfUser(UserEntity user, @Nullable Long targetId) {
        if (user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<RentLink> rentLinkList = rentalsLinkRepository.findAllBySeller_Id(targetId);
            return ResponseEntity.ok(rentLinkList.size());
        } else {
            List<RentLink> rentLinkList = rentalsLinkRepository.findAllByBuyer_Id(user.getId());
            return ResponseEntity.ok(rentLinkList.size());
        }
    }

    public ResponseEntity<?> getPurchasesNumberOfUser(UserEntity user, @Nullable Long targetId) {
        if (user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<PurchasesLinkEntity> purchasesList = purchasesLinkRepository.findAllBySeller_Id(targetId); //TODO fixare nel repository la query
            return ResponseEntity.ok(purchasesList.size());
        } else {
            List<PurchasesLinkEntity> purchasesList = purchasesLinkRepository.findByBuyer_Id(user.getId()); //TODO fixare nel repository la query
            return ResponseEntity.ok(purchasesList.size());
        }
    }

    public ResponseEntity<?> getOrdersNumberOfUser(UserEntity user,@Nullable Long targetId) {
        if (user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<OrdersLinkEntity> ordersList = ordersLinkRepository.findAllBySeller_Id(targetId); //TODO fixare nel repository la query
            return ResponseEntity.ok(ordersList.size());
        } else {
            List<OrdersLinkEntity> ordersList = ordersLinkRepository.findAllByBuyer_Id(user.getId()); //TODO fixare nel repository la query
            return ResponseEntity.ok(ordersList.size());
        }
    }

    public StatisticsDTO getAllDelearshipStatistics(UserEntity user) {
        if (user.getUserType() == UserTypes.ADMIN) {

            List<OrdersLinkEntity> orderList = ordersLinkRepository.findAll();
            List<RentLink> rentLinkList = rentalsLinkRepository.findAll();
            List<PurchasesLinkEntity> purchasesLinkList = purchasesLinkRepository.findAll();
            return new StatisticsDTO(orderList.size(), purchasesLinkList.size(), rentLinkList.size(), orderList.stream().map(orderLink -> OrderModel.modelToDto(OrderModel.entityToModel(orderLink.getOrder()))).toList(),
                    purchasesLinkList.stream().map(purchasesLink -> PurchaseModel.modelToDto(PurchaseModel.entityToModel(purchasesLink.getPurchase()))).toList(),
                    rentLinkList.stream().map(rentLink -> RentModel.modelToDTO(RentModel.entityToModel(rentLink.getRent()))).toList());
        }
        return null;
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
    public BigDecimal getDealershipRevenue(UserEntity user) {
        if (user.getUserType() == UserTypes.ADMIN) {
            BigDecimal sellerSalesRevenue = purchaseRepository.getFullPurchasePriceCount();
            BigDecimal sellerRentsRevenue = rentRepository.getTotalCostSum();
            BigDecimal sellerOrdersRevenue = orderRepository.getDepositOrderSum();
            BigDecimal sellerOrdersRevenue1 = orderRepository.getFullPaidOrderSum();

            return BigDecimal.ZERO.add(sellerSalesRevenue).add(sellerRentsRevenue).add(sellerOrdersRevenue).add(sellerOrdersRevenue1);
        } else {
            return null;
        }
    }

    public Map<String, Object> getDealershipStatistics() {
        // TODO Implementazione per recuperare le statistiche complessive del concessionario dal database
        return null;
    }

    public Map<String, Object> getUserStatistics(UserEntity user) {
        // TODO Implementazione per recuperare le statistiche complessive per un singolo utente dal database
        return null;
    }


    public Double getCustomerReturnFrequency() {
        return null;
    }

    public Map<String, Integer> getVehicleCountByBrandOrModel() {
        // TODO Implementazione per recuperare il numero di veicoli per marca o modello dal database
        return null;
    }

}