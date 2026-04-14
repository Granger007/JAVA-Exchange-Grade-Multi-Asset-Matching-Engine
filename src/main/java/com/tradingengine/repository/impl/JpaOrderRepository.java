package com.tradingengine.repository.impl;

import com.tradingengine.domain.model.Order;
import com.tradingengine.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaOrderRepository implements OrderRepository {

    private final SpringDataOrderRepository jpaRepository;

    public JpaOrderRepository(SpringDataOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        return jpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(String orderId) {
        return jpaRepository.findById(orderId);
    }

    @Override
    public List<Order> findByTraderId(String traderId) {
        return jpaRepository.findByTraderId(traderId);
    }

    @Override
    public List<Order> findActiveOrdersBySymbol(String symbol) {
        return jpaRepository.findActiveOrdersBySymbol(symbol);
    }

    @Override
    public List<Order> findBySymbol(String symbol) {
        return jpaRepository.findBySymbol(symbol);
    }

    @Override
    public boolean deleteById(String orderId) {
        if (jpaRepository.existsById(orderId)) {
            jpaRepository.deleteById(orderId);
            return true;
        }
        return false;
    }

    @Override
    public long countByStatus(String status) {
        return jpaRepository.countByOrderStatus(status);
    }

    @Override
    public boolean existsById(String orderId) {
        return jpaRepository.existsById(orderId);
    }
    
    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
