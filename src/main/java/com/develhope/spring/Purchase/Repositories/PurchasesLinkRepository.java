package com.develhope.spring.Purchase.Repositories;

import com.develhope.spring.Purchase.Entities.PurchasesLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasesLinkRepository extends JpaRepository<PurchasesLinkEntity, Long> {
    List<PurchasesLinkEntity> findByBuyer_Id(Long buyerId);

    List<PurchasesLinkEntity> findAllBySeller_Id(Long targetId);

    List<PurchasesLinkEntity> findAllByBuyer_Id(Long id);
}
