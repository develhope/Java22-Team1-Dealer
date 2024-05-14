package com.develhope.spring.dealershipStatistics.service;

import com.develhope.spring.Purchase.Entities.PurchasesLinkEntity;
import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import com.develhope.spring.Purchase.Repositories.PurchasesLinkRepository;
import com.develhope.spring.Rent.Entities.RentLink;
import com.develhope.spring.Rent.Repositories.RentalsLinkRepository;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.order.Repositories.OrderRepository;
import com.develhope.spring.order.Repositories.OrdersLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
            List<PurchasesLinkEntity> purchasesList = purchasesLinkRepository.findAllByBuyer_Id(user.getId()); //TODO fixare nel repository la query
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

}