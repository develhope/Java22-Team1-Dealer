package com.develhope.spring.Purchase.Repositories;

import com.develhope.spring.Purchase.Entities.PurchasesLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchasesLinkRepository extends JpaRepository<PurchasesLinkEntity, Long> {
    List<PurchasesLinkEntity> findByBuyer_Id(Long buyerId);

    List<PurchasesLinkEntity> findAllBySeller_Id(Long sellerId);
//    @Query("SELECT ple FROM PurchasesLinkEntity ple WHERE ple.seller.id = :sellerId AND ple.purchase.purchaseDate BETWEEN :startDate AND :endDate")
    @Query(value = "SELECT * FROM purchases_link WHERE purchases_link.seller.id = :sellerId AND purchases_link.purchase.purchaseDate BETWEEN :startDate AND :endDate", nativeQuery = true)
    List<PurchasesLinkEntity> findAllBySellerIdInBetweenDates(Long sellerId, LocalDate startDate, LocalDate endDate);

    PurchasesLinkEntity findByPurchase_PurchaseId(Long purchaseId);

}
