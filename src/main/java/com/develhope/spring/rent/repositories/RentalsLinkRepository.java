package com.develhope.spring.rent.repositories;

import com.develhope.spring.rent.entities.RentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalsLinkRepository extends JpaRepository<RentLink, Long> {
    List<RentLink> findAllByBuyer_Id(Long userId);
    RentLink findAllByRent_Id(Long userId);
    List<RentLink> findAllBySeller_Id(Long userId);
    @Query("SELECT rl FROM RentLink rl WHERE rl.seller.id = :sellerId AND rl.rent.startDate BETWEEN :startTime AND :endTime")
    List<RentLink> findAllBySellerIdBetweenDates(Long sellerId, LocalDate startTime, LocalDate endTime);
    @Query("SELECT rl FROM RentLink rl WHERE rl.rent.startDate BETWEEN :startTime AND :endTime")
    List<RentLink> findAllBetweenDates( LocalDate startTime, LocalDate endTime);

    @Query("SELECT rl FROM RentLink rl WHERE rl.rent.id = :rentId AND rl.buyer.id = :buyerId")
    Optional<RentLink> findByRentIdAndBuyerId(Long rentId, Long buyerId);
    @Query("SELECT rl FROM RentLink rl WHERE rl.rent.id = :rentId")
    Optional<RentLink> findByRentId(Long rentId);
}
