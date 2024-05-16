package com.develhope.spring.order.Repositories;

import com.develhope.spring.order.Entities.OrdersLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersLinkRepository extends JpaRepository<OrdersLinkEntity, Long> {
    List<OrdersLinkEntity> findByBuyer_Id(Long buyerId);

    OrdersLinkEntity findByOrder_OrderId(Long orderId);

    @Query("SELECT ole FROM OrdersLinkEntity ole WHERE ole.seller.id = :id")
    List<OrdersLinkEntity> findAllBySeller_Id(Long id);

    @Query("SELECT ole FROM OrdersLinkEntity ole WHERE ole.buyer.id = :id")
    List<OrdersLinkEntity> findAllByBuyer_Id(Long id);
}
