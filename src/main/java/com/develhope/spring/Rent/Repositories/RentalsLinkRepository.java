package com.develhope.spring.Rent.Repositories;

import com.develhope.spring.Rent.Entities.RentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalsLinkRepository extends JpaRepository<RentLink, Long> {
    List<RentLink> findAllByUserEntity_Id(Long userId);

    Optional<RentLink> findByIdAndUserEntity_Id(Long id, Long userEntityId);

}
