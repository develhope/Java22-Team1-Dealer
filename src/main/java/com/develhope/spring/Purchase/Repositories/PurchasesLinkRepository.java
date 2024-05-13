package com.develhope.spring.Purchase.Repositories;

import com.develhope.spring.Purchase.Entities.PurchasesLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchasesLinkRepository extends JpaRepository<PurchasesLink, Long> {
    List<PurchasesLink> findByBuyer_Id(Long buyerId);

}
