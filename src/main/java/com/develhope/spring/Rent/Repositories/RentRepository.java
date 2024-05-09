package com.develhope.spring.Rent.Repositories;
import com.develhope.spring.Rent.Entities.RentEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RentRepository extends JpaRepository<RentEntity, Long> {
    List<RentEntity> findAllByUserId(Long userId);
    List<RentEntity> findAllActive();
    Optional<RentEntity> findByIdAndUserId(Long id, Long userId);
}
