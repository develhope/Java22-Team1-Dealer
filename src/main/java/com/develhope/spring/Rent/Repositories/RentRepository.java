package com.develhope.spring.Rent.Repositories;
import com.develhope.spring.Rent.Entities.RentEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface RentRepository extends JpaRepository<RentEntity, Long> {
    List<RentEntity> findAllByUserId(Long userId);

    default List<RentEntity> findAllActive() {
        return findAll().stream().filter(RentEntity::isActive).collect(Collectors.toList());
    }

    Optional<RentEntity> findByIdAndUserId(Long id, Long userId);
}
