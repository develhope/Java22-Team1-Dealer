package com.develhope.spring.User.Entities;


import com.develhope.spring.User.Entities.Enum.UserTypes;

import com.develhope.spring.order.Entities.OrderEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @OneToMany(mappedBy = "user") // Relazione con l'entità Order
    private List<OrderEntity> orderEntities;
}
