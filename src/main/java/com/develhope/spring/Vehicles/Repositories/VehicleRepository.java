package com.develhope.spring.Vehicles.Repositories;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    List<VehicleEntity> findByAccessoriesIn(List<String> accessories);
    List<VehicleEntity> findByDisplacementBetween(Integer minDisplacement, Integer maxDisplacement);
    List<VehicleEntity> findByPowerBetween(Integer minPower, Integer maxPower);
}
