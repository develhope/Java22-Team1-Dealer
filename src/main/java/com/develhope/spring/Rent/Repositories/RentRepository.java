package com.develhope.spring.Rent.Repositories;
/*
import com.develhope.spring.Rent.Entities.RentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface RentRepository extends JpaRepository<RentEntity, Long> {
    List<RentEntity> findAllByUserEntity_Id(Long userId);

    default List<RentEntity> findAllActive() {
        return findAll().stream().filter(RentEntity::isActive).collect(Collectors.toList());
    }

    Optional<RentEntity> findByIdAndUserEntity_Id(Long id, Long userEntityId);

}
*/