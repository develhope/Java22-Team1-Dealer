package com.develhope.spring.Vehicles.Repositories;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import com.develhope.spring.Vehicles.Entities.VehicleStatus;
import com.develhope.spring.Vehicles.Entities.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    List<VehicleEntity> findByAccessoriesIn(List<String> accessories);
    List<VehicleEntity> findByDisplacementBetween(Integer minDisplacement, Integer maxDisplacement);
    List<VehicleEntity> findByPowerBetween(Integer minPower, Integer maxPower);
    List<VehicleEntity> findByRegistrationYearBetween(Integer minRegistrationYear, Integer maxRegistrationYear);
    List<VehicleEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<VehicleEntity> findByDiscountBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<VehicleEntity> findByIsNew(boolean isNew);
    List<VehicleEntity> findByVehicleStatus(VehicleStatus vehicleStatus);

    List<VehicleEntity> findByVehicleType(VehicleType vehicleType);
}
