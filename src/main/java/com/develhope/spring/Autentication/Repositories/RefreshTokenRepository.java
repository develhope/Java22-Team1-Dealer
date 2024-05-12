package com.develhope.spring.Autentication.Repositories;

import com.develhope.spring.Autentication.Entities.RefreshToken;
import com.develhope.spring.User.Entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUserEntityInfo(UserEntity userEntity);
}
