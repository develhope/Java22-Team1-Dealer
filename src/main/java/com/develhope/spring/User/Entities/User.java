package com.develhope.spring.User.Entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "User name")
    private String name;
    @Column(nullable = false, name = "User surname")
    private String surname;
    @Column(name = "User phoneNumber", unique = true)
    private String phoneNumber;
    @Column(nullable = false, unique = true, name = "User email")
    private String email;
    @Column(nullable = false, name = "User password")
    private String password;
    @Column(nullable = false, name = "User type")
    private UserTypes userType;
}
