package com.develhope.spring.order.Repositories;

import com.develhope.spring.order.Entities.OrdersLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersLinkRepository extends JpaRepository<OrdersLink, Long> {
    List<OrdersLink> findByBuyer_Id(Long buyerId);
}
