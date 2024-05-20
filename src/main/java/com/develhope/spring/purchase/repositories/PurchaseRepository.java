package com.develhope.spring.purchase.repositories;

import com.develhope.spring.purchase.entities.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {
    @Query(value = "SELECT SUM(ve.price) FROM purchases p JOIN vehicle_entity ve on p.vehicle_id = ve.vehicle_id WHERE p.is_paid = true", nativeQuery = true)
    BigDecimal getFullPurchasePriceCount();
}
