package com.develhope.spring.Rent.Entities;

import com.develhope.spring.User.Entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table
public class RentLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long linkId;

    @OneToOne
    @JoinColumn(name = "user_Id", nullable = false)
    UserEntity buyer;

    @OneToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    RentEntity rentEntity;

    @OneToOne
    @JoinColumn(name = "seller_id")
    UserEntity seller;


    public RentLink(UserEntity buyer, RentEntity rentEntity) {
        this.buyer = buyer;
        this.rentEntity = rentEntity;
    }

}