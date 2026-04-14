package com.tradingengine.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * ORDER - Core domain entity representing a trading order
 * 
 * GOOD DESIGN: Immutable business key, proper encapsulation, business logic in domain
 * SOLID: SRP - Only responsible for order state management
 * GRASP: Information Expert - Owns order validation logic
 */
public class Order {
    private final String orderId;
    private final String symbol;
    private final double price;
    private final long originalQuantity;
    private final OrderSide side;
    private final OrderType type;
    private final String traderId;
    private final Instant createdAt;
    
    private long quantity;
    private OrderStatus status;
    private Instant lastModified;

    public Order(String orderId, String symbol, double price, long quantity, 
                 OrderSide side, OrderType type, String traderId) {
        validateOrderData(orderId, symbol, price, quantity, side, type, traderId);
        
        this.orderId = orderId;
        this.symbol = symbol;
        this.price = type == OrderType.MARKET ? 0.0 : price; // Market orders can ignore price
        this.originalQuantity = quantity;
        this.quantity = quantity;
        this.side = side;
        this.type = type;
        this.traderId = traderId;
        this.status = OrderStatus.NEW;
        this.createdAt = Instant.now();
        this.lastModified = Instant.now();
    }

    // Business logic - Order owns its state transitions
    public void execute(long executedQuantity, double executionPrice) {
        validateExecution(executedQuantity, executionPrice);
        
        this.quantity -= executedQuantity;
        this.lastModified = Instant.now();
        
        if (this.quantity == 0) {
            this.status = OrderStatus.FILLED;
        } else if (this.status == OrderStatus.NEW) {
            this.status = OrderStatus.PARTIALLY_FILLED;
        }
    }

    public void cancel() {
        if (this.status == OrderStatus.FILLED) {
            throw new IllegalStateException("Cannot cancel filled order");
        }
        this.status = OrderStatus.CANCELLED;
        this.lastModified = Instant.now();
    }

    public void reject() {
        if (this.status != OrderStatus.NEW) {
            throw new IllegalStateException("Can only reject new orders");
        }
        this.status = OrderStatus.REJECTED;
        this.lastModified = Instant.now();
    }

    // Validation logic - Information Expert principle
    private void validateOrderData(String orderId, String symbol, double price, 
                                  long quantity, OrderSide side, OrderType type, String traderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty");
        }
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Order type cannot be null");
        }
        if (type == OrderType.LIMIT && price <= 0) {
            throw new IllegalArgumentException("Price must be positive for limit orders");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (side == null) {
            throw new IllegalArgumentException("Order side cannot be null");
        }
        if (traderId == null || traderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Trader ID cannot be null or empty");
        }
    }

    private void validateExecution(long executedQuantity, double executionPrice) {
        if (executedQuantity <= 0) {
            throw new IllegalArgumentException("Executed quantity must be positive");
        }
        if (executedQuantity > this.quantity) {
            throw new IllegalArgumentException("Executed quantity exceeds remaining quantity");
        }
        if (executionPrice <= 0) {
            throw new IllegalArgumentException("Execution price must be positive");
        }
    }

    // Getters - No setters for immutable fields
    public String getOrderId() { return orderId; }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public long getQuantity() { return quantity; }
    public long getOriginalQuantity() { return originalQuantity; }
    public OrderSide getSide() { return side; }
    public OrderType getType() { return type; }
    public String getTraderId() { return traderId; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastModified() { return lastModified; }

    public boolean isFilled() { return status == OrderStatus.FILLED; }
    public boolean isCancelled() { return status == OrderStatus.CANCELLED; }
    public boolean isRejected() { return status == OrderStatus.REJECTED; }
    public boolean isActive() { 
        return status == OrderStatus.NEW || status == OrderStatus.PARTIALLY_FILLED; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return String.format("Order{id='%s', symbol='%s', side=%s, type=%s, price=%.2f, " +
                           "quantity=%d, originalQuantity=%d, status=%s, trader='%s'}",
                           orderId, symbol, side, type, price, quantity, originalQuantity, status, traderId);
    }
}
