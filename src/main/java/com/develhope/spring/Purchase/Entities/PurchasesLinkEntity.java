package com.develhope.spring.Purchase.Entities;

import com.develhope.spring.User.Entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "purchases_link")
public class PurchasesLinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long linkId;

    @OneToOne
    @JoinColumn(name = "user_Id", nullable = false)
    UserEntity buyer;

    @OneToOne
    @JoinColumn(name = "purchase_id", nullable = false)
    PurchaseEntity purchase;

    @OneToOne
    @JoinColumn(name = "seller_id")
    UserEntity seller;

    public PurchasesLinkEntity(UserEntity buyer, PurchaseEntity purchaseEntity) {
        this.buyer = buyer;
        this.purchase = purchaseEntity;
    }
}
