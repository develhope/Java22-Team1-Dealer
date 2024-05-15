package com.develhope.spring.dealershipStatistics.service;

import com.develhope.spring.Purchase.Entities.PurchasesLinkEntity;
import com.develhope.spring.Purchase.Repositories.PurchasesLinkRepository;
import com.develhope.spring.Rent.Entities.RentLink;
import com.develhope.spring.Rent.Repositories.RentalsLinkRepository;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import com.develhope.spring.Vehicles.Repositories.VehicleRepository;
import com.develhope.spring.order.Entities.OrderEntity;
import com.develhope.spring.order.Entities.OrdersLinkEntity;
import com.develhope.spring.order.Repositories.OrdersLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Integer getRentsNumberOfUser(UserEntity user, Long targetId) {
        if(user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if(userOptional.isEmpty()) {
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
        if(user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if(userOptional.isEmpty()) {
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
        if(user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if(userOptional.isEmpty()) {
                return null;
            }
            List<PurchasesLinkEntity> ordersList = ordersLinkRepository.findAllBySeller_Id(targetId); //TODO fixare nel repository la query
            return ordersList.size();
        } else {
            List<PurchasesLinkEntity> ordersList = ordersLinkRepository.findAllByBuyer_Id(user.getId()); //TODO fixare nel repository la query
            return ordersList.size();
        }
    }

    public Integer getTotalOrders() {
        List<OrdersLinkEntity> orderList = ordersLinkRepository.findAll();
        return orderList.size();

        //TODO implementare il controllo degli users
    }

    public Integer getTotalPurchases() {
        List<PurchasesLinkEntity> purchaseList = purchasesLinkRepository.findAll();
        return purchaseList.size();

        //TODO implementare il controllo degli users
    }

    public Integer getTotalRentals() {
        List<RentLink> rentList = rentalsLinkRepository.findAll();
        return rentList.size();

        //TODO implementare il controllo degli users
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

    public Map<String, Object> getDealershipStatistics() {
        // TODO Implementazione per recuperare le statistiche complessive del concessionario dal database
    }

    public Map<String, Object> getUserStatistics(UserEntity user) {
        // TODO Implementazione per recuperare le statistiche complessive per un singolo utente dal database
    }

    public Map<String, Object> getFinancialStatistics() {
        // TODO Implementazione per recuperare le statistiche monetarie dal database
    }

    public Map<VehicleType, Integer> getVehicleCountByType() {
        // TODO Implementazione per recuperare il numero di veicoli per tipo dal database
    }

    public Map<String, Integer> getVehicleCountByBrandOrModel() {
        // TODO Implementazione per recuperare il numero di veicoli per marca o modello dal database
    }

    public Map<String, Object> getStatisticsByTimePeriod(String timePeriod) {
        // TODO Implementazione per recuperare le statistiche per periodo di tempo dal database
    }

    public Map<String, Object> getSellerPerformanceStatistics() {
        // TODO Implementazione per recuperare le statistiche di performance del personale dal database
    }
}