package com.tradingengine.repository;

import com.tradingengine.domain.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * ORDER REPOSITORY - Abstraction for order persistence
 * 
 * GOOD DESIGN: Clean repository interface, separation from implementation
 * DDD: Repository pattern for domain object persistence
 * SOLID: DIP - Service depends on abstraction, not concrete implementation
 */
public interface OrderRepository {
    
    /**
     * Save order (create or update)
     */
    Order save(Order order);
    
    /**
     * Find order by ID
     */
    Optional<Order> findById(String orderId);
    
    /**
     * Find all orders for a trader
     */
    List<Order> findByTraderId(String traderId);
    
    /**
     * Find active orders for a symbol
     */
    List<Order> findActiveOrdersBySymbol(String symbol);
    
    /**
     * Find all orders for a symbol
     */
    List<Order> findBySymbol(String symbol);
    
    /**
     * Delete order by ID
     */
    boolean deleteById(String orderId);
    
    /**
     * Count orders by status
     */
    long countByStatus(String status);
    
    /**
     * Check if order exists
     */
    boolean existsById(String orderId);
    
    /**
     * Delete all orders
     */
    void deleteAll();
}
