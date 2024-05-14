package com.develhope.spring.order.Repositories;

import com.develhope.spring.order.Entities.OrdersLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersLinkRepository extends JpaRepository<OrdersLinkEntity, Long> {
    List<OrdersLinkEntity> findByBuyer_Id(Long buyerId);

    OrdersLinkEntity findByOrder_Id(Long orderId);
}
