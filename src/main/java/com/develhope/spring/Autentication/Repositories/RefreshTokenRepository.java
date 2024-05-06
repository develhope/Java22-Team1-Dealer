package com.develhope.spring.Autentication.Repositories;

import com.develhope.spring.Autentication.Entities.DTO.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
