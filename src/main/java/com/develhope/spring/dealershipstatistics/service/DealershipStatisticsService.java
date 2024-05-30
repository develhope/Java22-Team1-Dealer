package com.develhope.spring.dealershipstatistics.service;

import com.develhope.spring.dealershipstatistics.entities.StatisticsDTO;
import com.develhope.spring.order.DTO.OrderDTO;
import com.develhope.spring.order.entities.OrdersLinkEntity;
import com.develhope.spring.order.model.OrderModel;
import com.develhope.spring.order.repositories.OrderRepository;
import com.develhope.spring.order.repositories.OrdersLinkRepository;
import com.develhope.spring.purchase.DTO.PurchaseDTO;
import com.develhope.spring.purchase.entities.PurchasesLinkEntity;
import com.develhope.spring.purchase.model.PurchaseModel;
import com.develhope.spring.purchase.repositories.PurchaseRepository;
import com.develhope.spring.purchase.repositories.PurchasesLinkRepository;
import com.develhope.spring.rent.DTO.RentDTO;
import com.develhope.spring.rent.entities.RentLink;
import com.develhope.spring.rent.model.RentModel;
import com.develhope.spring.rent.repositories.RentRepository;
import com.develhope.spring.rent.repositories.RentalsLinkRepository;
import com.develhope.spring.user.entities.Enum.UserTypes;
import com.develhope.spring.user.entities.UserEntity;
import com.develhope.spring.user.repositories.UserRepository;
import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleType;
import com.develhope.spring.vehicles.repositories.VehicleRepository;
import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Getter
    private Map<String, Long> mostOrderedVehicles = new HashMap<>();
    @Getter
    private Map<String, Long> mostSoldVehicles = new HashMap<>();
    @Getter
    private Map<String, Integer> mostSearchedVehicles = new HashMap<>();
    @Getter
    private VehicleEntity highestSalePriceVehicle;
    @Getter
    private BigDecimal highestSalePrice = BigDecimal.ZERO;

    public void updateOrderStatistics(VehicleEntity vehicle) {
        String vehicleKey = vehicle.getBrand() + " " + vehicle.getModel();
        mostOrderedVehicles.put(vehicleKey, mostOrderedVehicles.getOrDefault(vehicleKey, 0L) + 1);
    }

    public void removeOrderStatistics(VehicleEntity vehicle) {
        String vehicleKey = vehicle.getBrand() + " " + vehicle.getModel();
        mostOrderedVehicles.put(vehicleKey, mostOrderedVehicles.get(vehicleKey) - 1);
    }

    public void updatePurchaseStatistics(VehicleEntity vehicle) {
        BigDecimal salePrice = vehicle.getPrice();
        String vehicleKey = vehicle.getBrand() + " " + vehicle.getModel();
        mostSoldVehicles.put(vehicleKey, mostSoldVehicles.getOrDefault(vehicleKey, 0L) + 1);
        if (salePrice.compareTo(highestSalePrice) > 0) {
            highestSalePrice = salePrice;
            highestSalePriceVehicle = vehicle;
        }
    }

    public void removePurchaseStatistics(VehicleEntity vehicle) {
        String vehicleKey = vehicle.getBrand() + " " + vehicle.getModel();
        mostSoldVehicles.put(vehicleKey, mostSoldVehicles.get(vehicleKey) - 1);
    }

    public void updateSearchStatistics(String brand, String model) {
        String vehicleKey = brand + " " + model;
        mostSearchedVehicles.put(vehicleKey, mostSearchedVehicles.getOrDefault(vehicleKey, 0) + 1);
    }

    public ResponseEntity<?> getRentsNumberOfUser(UserEntity user, @Nullable Long targetId) {
        if (user.getUserType() == UserTypes.ADMIN) {
            return handleAdminRentsRequest(targetId);
        } else if (user.getUserType() == UserTypes.SELLER || user.getUserType() == UserTypes.BUYER) {
            return handleUserRentsRequest(user);
        } else {
            return new ResponseEntity<>("User type not authorized.", HttpStatus.UNAUTHORIZED);
        }
    }

    private ResponseEntity<?> handleAdminRentsRequest(@Nullable Long targetId) {
        if (targetId == null) {
            return ResponseEntity.badRequest().body("Target ID is required for admin requests.");
        }
        Optional<UserEntity> userOptional = userRepository.findById(targetId);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        List<RentLink> rentLinkList = rentalsLinkRepository.findAllBySeller_Id(targetId);
        return ResponseEntity.ok(rentLinkList.size());
    }

    private ResponseEntity<?> handleUserRentsRequest(UserEntity user) {
        List<RentLink> rentLinkList = rentalsLinkRepository.findAllByBuyer_Id(user.getId());
        return ResponseEntity.ok(rentLinkList.size());
    }

    public ResponseEntity<?> getPurchasesNumberOfUser(UserEntity user, @Nullable Long targetId) {
        if (user.getUserType() == UserTypes.ADMIN) {
            return handleAdminPurchasesRequest(targetId);
        } else if (user.getUserType() == UserTypes.SELLER || user.getUserType() == UserTypes.BUYER) {
            return handleUserPurchasesRequest(user);
        } else {
            return new ResponseEntity<>("User type not authorized.", HttpStatus.UNAUTHORIZED);
        }
    }

    private ResponseEntity<?> handleAdminPurchasesRequest(@Nullable Long targetId) {
        if (targetId == null) {
            return ResponseEntity.badRequest().body("Target ID is required for admin requests.");
        }
        Optional<UserEntity> userOptional = userRepository.findById(targetId);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        List<PurchasesLinkEntity> purchasesList = purchasesLinkRepository.findAllBySeller_Id(targetId);
        return ResponseEntity.ok(purchasesList.size());
    }

    private ResponseEntity<?> handleUserPurchasesRequest(UserEntity user) {
        List<PurchasesLinkEntity> purchasesList = purchasesLinkRepository.findByBuyer_Id(user.getId());
        return ResponseEntity.ok(purchasesList.size());
    }

    public ResponseEntity<?> getOrdersNumberOfUser(UserEntity user, @Nullable Long targetId) {
        if (user.getUserType() == UserTypes.ADMIN) {
            return handleAdminOrdersRequest(targetId);
        } else if (user.getUserType() == UserTypes.SELLER || user.getUserType() == UserTypes.BUYER) {
            return handleUserOrdersRequest(user);
        } else {
            return new ResponseEntity<>("User type not authorized.", HttpStatus.UNAUTHORIZED);
        }
    }

    private ResponseEntity<?> handleAdminOrdersRequest(@Nullable Long targetId) {
        if (targetId == null) {
            return ResponseEntity.badRequest().body("Target ID is required for admin requests.");
        }
        Optional<UserEntity> userOptional = userRepository.findById(targetId);
        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
        List<OrdersLinkEntity> ordersList = ordersLinkRepository.findAllBySeller_Id(targetId);
        return ResponseEntity.ok(ordersList.size());
    }

    private ResponseEntity<?> handleUserOrdersRequest(UserEntity user) {
        List<OrdersLinkEntity> ordersList = ordersLinkRepository.findAllByBuyer_Id(user.getId());
        return ResponseEntity.ok(ordersList.size());
    }

    public ResponseEntity<?> getAllDelearshipStatistics(UserEntity user) {
        if (user.getUserType() == UserTypes.ADMIN) {
            List<OrdersLinkEntity> orderList = ordersLinkRepository.findAll();
            List<RentLink> rentLinkList = rentalsLinkRepository.findAll();
            List<PurchasesLinkEntity> purchasesLinkList = purchasesLinkRepository.findAll();

            List<OrderDTO> orders = orderList.stream()
                    .map(orderLink -> OrderModel.modelToDto(OrderModel.entityToModel(orderLink.getOrder())))
                    .collect(Collectors.toList());

            List<PurchaseDTO> purchases = purchasesLinkList.stream()
                    .map(purchasesLink -> PurchaseModel.modelToDto(PurchaseModel.entityToModel(purchasesLink.getPurchase())))
                    .collect(Collectors.toList());

            List<RentDTO> rents = rentLinkList.stream()
                    .map(rentLink -> RentModel.modelToDTO(RentModel.entityToModel(rentLink.getRent())))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new StatisticsDTO(orderList.size(), purchasesLinkList.size(), rentLinkList.size(), orders, purchases, rents));
        }
        return new ResponseEntity<>("Only admins can access dealership statistics.", HttpStatus.UNAUTHORIZED);
    }

    public Map<VehicleType, Integer> getVehicleCountByType() {
        return vehicleRepository.findAll().stream()
                .collect(Collectors.groupingBy(VehicleEntity::getVehicleType, Collectors.summingInt(v -> 1)));
    }

    public ResponseEntity<?> getSellerSalesByTimePeriod(UserEntity user, Long sellerId, LocalDate startDate, LocalDate endDate) {
        if (user.getUserType() != UserTypes.ADMIN) {
            return new ResponseEntity<>("Only admins can access this information.", HttpStatus.NOT_FOUND);
        }

        List<PurchasesLinkEntity> sellerSales = purchasesLinkRepository.findAllBySellerIdInBetweenDates(sellerId, startDate, endDate);
        if (sellerSales.isEmpty()) {
            return new ResponseEntity<>("User type not authorized.", HttpStatus.NOT_FOUND);
        }

        UserEntity seller = sellerSales.getFirst().getSeller();
        return ResponseEntity.ok(Map.of(seller, sellerSales.size()));
    }


    public ResponseEntity<?> getSellerRevenueByTimePeriod(UserEntity user, Long sellerId, LocalDate startDate, LocalDate endDate) {
        if (user.getUserType() != UserTypes.ADMIN) {
            return new ResponseEntity<>("Only admins can access this information.", HttpStatus.NOT_FOUND);
        }

        UserEntity seller = userRepository.findById(sellerId)
                .filter(u -> u.getUserType() == UserTypes.SELLER)
                .orElse(null);

        if (seller == null) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        BigDecimal totalRevenue = calculateTotalRevenue(sellerId, startDate, endDate);

        return ResponseEntity.ok(Map.of(seller, totalRevenue));
    }

    private BigDecimal calculateTotalRevenue(Long sellerId, LocalDate startDate, LocalDate endDate) {
        BigDecimal sellerSalesRevenue = purchasesLinkRepository.findAllBySellerIdInBetweenDates(sellerId, startDate, endDate).stream()
                .map(sale -> sale.getPurchase().getVehicle().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sellerRentsRevenue = rentalsLinkRepository.findAllBySellerIdBetweenDates(sellerId, startDate, endDate).stream()
                .map(rentLink -> rentLink.getRent().getTotalCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sellerOrdersRevenue = ordersLinkRepository.findAllBySelleridBetweenDates(sellerId, startDate, endDate).stream()
                .map(ordersLink -> ordersLink.getOrder().getIsPaid() ? ordersLink.getOrder().getVehicle().getPrice() : ordersLink.getOrder().getDeposit())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sellerSalesRevenue.add(sellerRentsRevenue).add(sellerOrdersRevenue);
    }

    public ResponseEntity<?> getDealershipRevenueByTimePeriod(UserEntity user, LocalDate startDate, LocalDate endDate) {
        if (user.getUserType() != UserTypes.ADMIN) {
            return new ResponseEntity<>("Only admins can access this information.", HttpStatus.NOT_FOUND);
        }

        BigDecimal totalRevenue = calculateTotalRevenueForDealership(startDate, endDate);

        return ResponseEntity.ok(totalRevenue);
    }

    private BigDecimal calculateTotalRevenueForDealership(LocalDate startDate, LocalDate endDate) {
        BigDecimal salesRevenue = purchasesLinkRepository.findAllInBetweenDates(startDate, endDate).stream()
                .map(sale -> sale.getPurchase().getVehicle().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal rentsRevenue = rentalsLinkRepository.findAllBetweenDates(startDate, endDate).stream()
                .map(rentLink -> rentLink.getRent().getTotalCost())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ordersRevenue = ordersLinkRepository.findAllBetweenDates(startDate, endDate).stream()
                .map(ordersLink -> ordersLink.getOrder().getIsPaid() ? ordersLink.getOrder().getVehicle().getPrice() : ordersLink.getOrder().getDeposit())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return salesRevenue.add(rentsRevenue).add(ordersRevenue);
    }

    public ResponseEntity<?> getDealershipRevenue(UserEntity user) {
        if (user.getUserType() != UserTypes.ADMIN) {
            return new ResponseEntity<>("Only admins can access this information.", HttpStatus.NOT_FOUND);
        }

        BigDecimal salesRevenue = purchaseRepository.getFullPurchasePriceCount();
        BigDecimal rentsRevenue = rentRepository.getTotalCostSum();
        BigDecimal ordersDepositRevenue = orderRepository.getDepositOrderSum();
        BigDecimal ordersPaidRevenue = orderRepository.getFullPaidOrderSum();

        return ResponseEntity.ok(salesRevenue.add(rentsRevenue).add(ordersDepositRevenue).add(ordersPaidRevenue));
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