package com.tradingengine.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * TRADE - Immutable domain entity representing executed trade
 * 
 * GOOD DESIGN: Immutable after creation, proper value object semantics
 * GRASP: Creator - MatchingEngine creates trades, Information Expert for trade calculations
 */
@Entity
@Table(name = "trades")
public class Trade {
    @Id
    private String tradeId;
    private String buyOrderId;
    private String sellOrderId;
    private String buyTraderId;
    private String sellTraderId;
    private String symbol;
    private double price;
    private long quantity;
    private double notionalValue;
    private Instant executedAt;
    
    protected Trade() {} // JPA requires no-arg constructor

    public Trade(String tradeId, String buyOrderId, String sellOrderId,
                 String buyTraderId, String sellTraderId, String symbol,
                 double price, long quantity) {
        validateTradeData(tradeId, buyOrderId, sellOrderId, buyTraderId, 
                         sellTraderId, symbol, price, quantity);
        
        this.tradeId = tradeId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.buyTraderId = buyTraderId;
        this.sellTraderId = sellTraderId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.notionalValue = calculateNotionalValue(price, quantity);
        this.executedAt = Instant.now();
    }

    // Business logic - Trade owns its calculations
    private double calculateNotionalValue(double price, long quantity) {
        return BigDecimal.valueOf(price)
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private void validateTradeData(String tradeId, String buyOrderId, String sellOrderId,
                                  String buyTraderId, String sellTraderId, String symbol,
                                  double price, long quantity) {
        if (tradeId == null || tradeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Trade ID cannot be null or empty");
        }
        if (buyOrderId == null || buyOrderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buy order ID cannot be null or empty");
        }
        if (sellOrderId == null || sellOrderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Sell order ID cannot be null or empty");
        }
        if (buyTraderId == null || buyTraderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Buy trader ID cannot be null or empty");
        }
        if (sellTraderId == null || sellTraderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Sell trader ID cannot be null or empty");
        }
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Trade price must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Trade quantity must be positive");
        }
    }

    // Getters - All immutable
    public String getTradeId() { return tradeId; }
    public String getBuyOrderId() { return buyOrderId; }
    public String getSellOrderId() { return sellOrderId; }
    public String getBuyTraderId() { return buyTraderId; }
    public String getSellTraderId() { return sellTraderId; }
    public String getSymbol() { return symbol; }
    public double getPrice() { return price; }
    public long getQuantity() { return quantity; }
    public double getNotionalValue() { return notionalValue; }
    public Instant getExecutedAt() { return executedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return Objects.equals(tradeId, trade.tradeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tradeId);
    }

    @Override
    public String toString() {
        return String.format("Trade{id='%s', symbol='%s', price=%.2f, quantity=%d, " +
                           "notional=%.2f, executedAt=%s}",
                           tradeId, symbol, price, quantity, notionalValue, executedAt);
    }
}
