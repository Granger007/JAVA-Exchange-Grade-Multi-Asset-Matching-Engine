package com.tradingengine.repository.impl;

import com.tradingengine.domain.model.Order;
import com.tradingengine.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IN-MEMORY ORDER REPOSITORY - Simple implementation for demonstration
 * 
 * GOOD DESIGN: Implements repository interface, clean separation
 * In production, this would be replaced with JPA/JDBC implementation
 */
public class InMemoryOrderRepository implements OrderRepository {
    
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    
    @Override
    public Order save(Order order) {
        orders.put(order.getOrderId(), order);
        return order;
    }
    
    @Override
    public Optional<Order> findById(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
    
    @Override
    public List<Order> findByTraderId(String traderId) {
        return orders.values().stream()
                .filter(order -> order.getTraderId().equals(traderId))
                .toList();
    }
    
    @Override
    public List<Order> findActiveOrdersBySymbol(String symbol) {
        return orders.values().stream()
                .filter(order -> order.getSymbol().equals(symbol))
                .filter(Order::isActive)
                .toList();
    }
    
    @Override
    public List<Order> findBySymbol(String symbol) {
        return orders.values().stream()
                .filter(order -> order.getSymbol().equals(symbol))
                .toList();
    }
    
    @Override
    public boolean deleteById(String orderId) {
        return orders.remove(orderId) != null;
    }
    
    @Override
    public long countByStatus(String status) {
        return orders.values().stream()
                .filter(order -> order.getStatus().toString().equals(status))
                .count();
    }
    
    @Override
    public boolean existsById(String orderId) {
        return orders.containsKey(orderId);
    }
}
