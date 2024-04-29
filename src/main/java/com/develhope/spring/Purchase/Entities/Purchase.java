package com.develhope.spring.Purchase.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idPurchase;
    @Column(nullable = false, name = "deposito")
    private int deposit;
    @Column(nullable = false, name = "pagato")
    private boolean payed;
    @Column(nullable = false, name = "stato_ordine")
    private String status;
    @Column(nullable = false, name = "ordinato/venduto")
    private boolean isSelled;
}
