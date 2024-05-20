package com.develhope.spring.rent.entities;

import com.develhope.spring.User.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class RentLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long linkId;

    @ManyToOne
    @JoinColumn(name = "user_Id", nullable = false)
    UserEntity buyer;

    @OneToOne
    @JoinColumn(name = "rent_id", nullable = false)
    RentEntity rent;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    UserEntity seller;


    public RentLink(UserEntity buyer, RentEntity rentEntity) {
        this.buyer = buyer;
        this.rent = rentEntity;
    }

    public RentLink(UserEntity buyer, RentEntity rentEntity, UserEntity seller) {
        this.buyer = buyer;
        this.rent = rentEntity;
        this.seller = seller;
    }

}
