package com.develhope.spring.Vehicles.Repositories;

import com.develhope.spring.Vehicles.Entities.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {
}
