package com.develhope.spring.autentication.entities;

import com.develhope.spring.user.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private Instant expDate;
    @OneToOne
    @JoinColumn(name = "userEntity", referencedColumnName = "id")
    private UserEntity userEntityInfo;
}
