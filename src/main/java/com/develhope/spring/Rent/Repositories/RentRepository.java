package com.develhope.spring.Rent.Repositories;

import com.develhope.spring.Rent.Entities.RentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentRepository extends JpaRepository<RentClass, Long> {
}
