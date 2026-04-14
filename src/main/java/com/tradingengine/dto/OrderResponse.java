package com.tradingengine.dto;

import com.tradingengine.domain.model.Trade;

import java.time.Instant;
import java.util.List;

/**
 * ORDER RESPONSE - DTO for order placement responses
 * 
 * GOOD DESIGN: Complete order information with execution details
 * MVC: Data Transfer Object for service-controller communication
 */
public class OrderResponse {
    private final String orderId;
    private final String symbol;
    private final double price;
    private final long quantity;
    private final String side;
    private final String status;
    private final String traderId;
    private final Instant createdAt;
    private final Instant lastModified;
    private final List<Trade> trades;

    public OrderResponse(String orderId, String symbol, double price, long quantity,
                        String side, String status, String traderId,
                        Instant createdAt, Instant lastModified, List<Trade> trades) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.status = status;
        this.traderId = traderId;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
        this.trades = trades;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public long getQuantity() { return quantity; }
    public String getSide() { return side; }
    public String getStatus() { return status; }
    public String getTraderId() { return traderId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastModified() { return lastModified; }
    public List<Trade> getTrades() { return trades; }

    @Override
    public String toString() {
        return String.format("OrderResponse{id='%s', symbol='%s', side=%s, price=%.2f, " +
                           "quantity=%d, status=%s, trades=%d}",
                           orderId, symbol, side, price, quantity, status, trades.size());
    }
}
