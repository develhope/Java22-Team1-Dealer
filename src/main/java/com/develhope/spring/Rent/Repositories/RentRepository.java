package com.develhope.spring.Rent.Repositories;

import com.develhope.spring.Rent.Entities.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {
    List<Rent> findByUserId(Long userId);
}
