package com.develhope.spring.dealershipStatistics.repositories;

import com.develhope.spring.dealershipStatistics.entities.UserStatisticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatisticsEntity, Long> {
}
