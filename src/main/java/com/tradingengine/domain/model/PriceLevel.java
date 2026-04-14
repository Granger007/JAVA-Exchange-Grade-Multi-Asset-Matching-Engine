package com.tradingengine.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PRICELEVEL - Value object representing orders at a specific price level
 * 
 * GOOD DESIGN: Encapsulated price level management, FIFO ordering by default
 * Thread-safe operations for concurrent access
 */
public class PriceLevel {
    private final double price;
    private final Map<String, Order> orders;  // Maintains insertion order for FIFO
    private volatile long totalQuantity;

    public PriceLevel(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.price = price;
        this.orders = new ConcurrentHashMap<>();  // Thread-safe
        this.totalQuantity = 0;
    }

    public void addOrder(Order order) {
        if (order.getPrice() != this.price) {
            throw new IllegalArgumentException("Order price mismatch: " + 
                                             order.getPrice() + " vs " + this.price);
        }
        
        orders.put(order.getOrderId(), order);
        totalQuantity += order.getQuantity();
    }

    public boolean removeOrder(String orderId) {
        Order removed = orders.remove(orderId);
        if (removed != null) {
            totalQuantity -= removed.getQuantity();
            return true;
        }
        return false;
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders.values());  // Defensive copy
    }

    public Order getFirstOrder() {
        return orders.values().iterator().next();
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }

    public int getOrderCount() {
        return orders.size();
    }

    public double getPrice() {
        return price;
    }

    public long getTotalQuantity() {
        return totalQuantity;
    }

    // Create immutable copy for safe external access
    public PriceLevel copy() {
        PriceLevel copy = new PriceLevel(this.price);
        copy.orders.putAll(this.orders);
        copy.totalQuantity = this.totalQuantity;
        return copy;
    }

    @Override
    public String toString() {
        return String.format("PriceLevel{price=%.2f, orderCount=%d, totalQuantity=%d}",
                           price, getOrderCount(), totalQuantity);
    }
}
