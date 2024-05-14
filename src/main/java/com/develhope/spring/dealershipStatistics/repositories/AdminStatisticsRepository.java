package com.develhope.spring.dealershipStatistics.repositories;

import com.develhope.spring.dealershipStatistics.entities.AdminStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminStatisticsRepository extends JpaRepository<AdminStatisticsEntity, Long> {
}
