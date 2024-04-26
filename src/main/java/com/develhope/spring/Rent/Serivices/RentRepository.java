package com.develhope.spring.Rent.Serivices;

import com.develhope.spring.Rent.Entities.Rent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentRepository extends JpaRepository<Rent, Long> {
}
