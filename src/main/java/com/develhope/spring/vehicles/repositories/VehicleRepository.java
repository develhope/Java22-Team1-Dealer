package com.develhope.spring.vehicles.repositories;

import com.develhope.spring.vehicles.entities.VehicleEntity;
import com.develhope.spring.vehicles.entities.VehicleStatus;
import com.develhope.spring.vehicles.entities.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
    List<VehicleEntity> findByColor(String color);

    List<VehicleEntity> findByModel(String model);

    List<VehicleEntity> findByBrand(String brand);

    List<VehicleEntity> findByTransmission(String transmission);

    List<VehicleEntity> findByPowerSupply(String powerSupply);

    List<VehicleEntity> findByAccessoriesIn(List<String> accessories);

    List<VehicleEntity> findByDisplacementBetween(Integer minDisplacement, Integer maxDisplacement);

    List<VehicleEntity> findByPowerBetween(Integer minPower, Integer maxPower);

    List<VehicleEntity> findByRegistrationYearBetween(Integer minRegistrationYear, Integer maxRegistrationYear);

    List<VehicleEntity> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<VehicleEntity> findByDiscountBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<VehicleEntity> findByIsNew(boolean isNew);

    List<VehicleEntity> findByVehicleStatus(VehicleStatus vehicleStatus);

    List<VehicleEntity> findByVehicleType(VehicleType vehicleType);

    @Query("SELECT v.brand, v.model, COUNT(v) FROM VehicleEntity v GROUP BY v.brand, v.model")
    List<Object[]> countVehiclesByBrandAndModel();
}
