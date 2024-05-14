package com.develhope.spring.dealershipStatistics.service;

import com.develhope.spring.Purchase.Repositories.PurchaseRepository;
import com.develhope.spring.Rent.Entities.RentLink;
import com.develhope.spring.Rent.Repositories.RentalsLinkRepository;
import com.develhope.spring.User.Entities.Enum.UserTypes;
import com.develhope.spring.User.Entities.UserEntity;
import com.develhope.spring.User.Repositories.UserRepository;
import com.develhope.spring.order.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Integer getRentsNumberOfUser(UserEntity user, Long targetId) {
        if(user.getUserType() == UserTypes.ADMIN) {
            Optional<UserEntity> userOptional = userRepository.findById(targetId);
            if(userOptional.isEmpty()) {
                return null;
            }
            List<RentLink> rentLinkList = rentalRepository.findAllBySeller_Id(targetId);
            return rentLinkList.size();
        } else {
            List<RentLink> rentLinkList = rentalRepository.findAllByBuyer_Id(user.getId());
            return rentLinkList.size();
        }
    }

}