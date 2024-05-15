package com.develhope.spring.Rent.Entities;

import com.develhope.spring.User.Entities.UserEntity;
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
    RentEntity rentEntity;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    UserEntity seller;


    public RentLink(UserEntity buyer, RentEntity rentEntity) {
        this.buyer = buyer;
        this.rentEntity = rentEntity;
    }

}
