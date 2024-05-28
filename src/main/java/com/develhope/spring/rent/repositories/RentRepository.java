package com.develhope.spring.rent.repositories;

import com.develhope.spring.rent.entities.RentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface RentRepository extends JpaRepository<RentEntity, Long> {
    @Query(value = "SELECT SUM(r.total_cost) FROM rentals r WHERE r.is_paid;", nativeQuery = true)
    BigDecimal getTotalCostSum();

}
