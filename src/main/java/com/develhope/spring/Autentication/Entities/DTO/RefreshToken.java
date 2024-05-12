package com.develhope.spring.Autentication.Entities.DTO;

import com.develhope.spring.User.Entities.UserEntity;
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
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private UserEntity userEntityInfo;
}
