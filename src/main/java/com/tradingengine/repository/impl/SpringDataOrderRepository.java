package com.tradingengine.repository.impl;

import com.tradingengine.domain.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataOrderRepository extends JpaRepository<Order, String> {
    List<Order> findByTraderId(String traderId);
    
    @Query("SELECT o FROM Order o WHERE o.symbol = :symbol AND (o.status = 'NEW' OR o.status = 'PARTIALLY_FILLED')")
    List<Order> findActiveOrdersBySymbol(@Param("symbol") String symbol);
    
    List<Order> findBySymbol(String symbol);
    
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByOrderStatus(@Param("status") String status);
}
