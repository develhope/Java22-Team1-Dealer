package com.develhope.spring.dealershipStatistics.repositories;

import com.develhope.spring.dealershipStatistics.entities.DealershipStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealershipStatisticsRepository extends JpaRepository<DealershipStatisticsEntity, Long> {
}
