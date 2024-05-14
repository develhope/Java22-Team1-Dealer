package com.develhope.spring.dealershipStatistics.repositories;

import com.develhope.spring.dealershipStatistics.entities.SellerStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerStatisticsRepository extends JpaRepository<SellerStatisticsEntity, Long> {
}
