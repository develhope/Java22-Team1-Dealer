package com.develhope.spring.order.Repositories;

import com.develhope.spring.order.Entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    @Query(value = "SELECT SUM(ve.price) FROM order_entity oe JOIN vehicle_entity ve on oe.vehicle_id = ve.vehicle_id  WHERE oe.paid = TRUE;", nativeQuery = true)
    BigDecimal getFullPaidOrderSum();

    @Query(value = "SELECT SUM(oe.deposit) FROM order_entity oe  WHERE oe.paid = FALSE;", nativeQuery = true)
    BigDecimal getDepositOrderSum();
}
