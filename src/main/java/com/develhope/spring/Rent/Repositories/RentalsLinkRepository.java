package com.develhope.spring.Rent.Repositories;

import com.develhope.spring.Rent.Entities.RentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalsLinkRepository extends JpaRepository<RentLink, Long> {
    List<RentLink> findAllByBuyer_Id(Long userId);

    @Query("SELECT rl FROM RentLink rl WHERE rl.rentEntity.id = :rentEntityId AND rl.buyer.id = :buyerId")
    Optional<RentLink> findByRentIdAndBuyerId(Long rentEntityId, Long buyerId);
}
