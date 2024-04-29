package com.develhope.spring.Purchase.Repositories;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<PurchaseEntity, Long> {
}
