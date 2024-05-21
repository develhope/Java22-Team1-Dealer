package com.develhope.spring.autentication.repositories;

import com.develhope.spring.autentication.entities.RefreshToken;
import com.develhope.spring.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUserEntityInfo(UserEntity userEntity);
}
