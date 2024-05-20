package com.develhope.spring.User.repositories;

import com.develhope.spring.User.entities.Enum.UserTypes;
import com.develhope.spring.User.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByUserType(UserTypes userTypes);

    Optional<UserEntity> findByNameAndSurname(String name, String surname);
}
