package com.develhope.spring.Purchase.Entities;

import com.develhope.spring.Purchase.Entities.DTO.PurchaseDTO;
import com.develhope.spring.Purchase.Entities.Enums.PurchaseStatus;
import com.develhope.spring.order.Entities.Order;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPurchase;
    @Column(nullable = false, name = "deposito")
    private int deposit;
    @Column(nullable = false, name = "pagato")
    private boolean isPaid;
    @Column(nullable = false, name = "stato_ordine")
    private PurchaseStatus status;


    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public PurchaseDTO toDTO() {
        return new PurchaseDTO(this.idPurchase, this.deposit, this.isPaid, this.status, this.order);
    }
}
