package com.develhope.spring.Rent.Serivices;

import com.develhope.spring.Rent.Entities.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {
}
