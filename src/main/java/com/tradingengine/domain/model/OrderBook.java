package com.tradingengine.domain.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * ORDERBOOK - Central limit order book with proper encapsulation
 * 
 * GOOD DESIGN: Encapsulated internal structure, thread-safe operations, clear API
 * SOLID: SRP - Only manages order book state and price levels
 * GRASP: Information Expert - Owns order book operations and price calculations
 */
public class OrderBook {
    private final String symbol;
    private final NavigableMap<Double, PriceLevel> bids;  // Descending order
    private final NavigableMap<Double, PriceLevel> asks;  // Ascending order
    private final Map<String, Order> orderIndex;          // Fast order lookup
    
    public OrderBook(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }
        
        this.symbol = symbol;
        // Thread-safe collections for concurrent access
        this.bids = new ConcurrentSkipListMap<>(Collections.reverseOrder());
        this.asks = new ConcurrentSkipListMap<>();
        this.orderIndex = new ConcurrentHashMap<>();
    }

    // Order management - OrderBook owns its state
    public void addOrder(Order order) {
        if (!order.getSymbol().equals(this.symbol)) {
            throw new IllegalArgumentException("Order symbol mismatch: " + 
                                             order.getSymbol() + " vs " + this.symbol);
        }
        if (!order.isActive()) {
            throw new IllegalStateException("Cannot add inactive order: " + order.getStatus());
        }

        NavigableMap<Double, PriceLevel> book = getBookForSide(order.getSide());
        PriceLevel level = book.computeIfAbsent(order.getPrice(), PriceLevel::new);
        level.addOrder(order);
        orderIndex.put(order.getOrderId(), order);
    }

    public boolean removeOrder(String orderId) {
        Order order = orderIndex.get(orderId);
        if (order == null) {
            return false;
        }

        NavigableMap<Double, PriceLevel> book = getBookForSide(order.getSide());
        PriceLevel level = book.get(order.getPrice());
        
        if (level != null) {
            level.removeOrder(orderId);
            if (level.isEmpty()) {
                book.remove(order.getPrice());
            }
        }
        
        orderIndex.remove(orderId);
        return true;
    }

    public Optional<Order> getOrder(String orderId) {
        return Optional.ofNullable(orderIndex.get(orderId));
    }

    // Price level access - Encapsulated, no direct exposure of internal structure
    public double getBestBid() {
        return bids.isEmpty() ? 0.0 : bids.firstKey();
    }

    public double getBestAsk() {
        return asks.isEmpty() ? 0.0 : asks.firstKey();
    }

    public double getMidPrice() {
        double bestBid = getBestBid();
        double bestAsk = getBestAsk();
        return (bestBid > 0 && bestAsk > 0) ? (bestBid + bestAsk) / 2.0 : 0.0;
    }

    public double getSpread() {
        double bestBid = getBestBid();
        double bestAsk = getBestAsk();
        return (bestBid > 0 && bestAsk > 0) ? bestAsk - bestBid : 0.0;
    }

    // Market depth - Returns immutable copies for safety
    public List<PriceLevel> getBidLevels(int maxDepth) {
        return getLevels(bids, maxDepth);
    }

    public List<PriceLevel> getAskLevels(int maxDepth) {
        return getLevels(asks, maxDepth);
    }

    private List<PriceLevel> getLevels(NavigableMap<Double, PriceLevel> book, int maxDepth) {
        return book.values().stream()
                .limit(maxDepth)
                .map(PriceLevel::copy)  // Return immutable copies
                .toList();
    }

    // Order book statistics
    public long getTotalBidQuantity() {
        return bids.values().stream()
                .mapToLong(PriceLevel::getTotalQuantity)
                .sum();
    }

    public long getTotalAskQuantity() {
        return asks.values().stream()
                .mapToLong(PriceLevel::getTotalQuantity)
                .sum();
    }

    public int getOrderCount() {
        return orderIndex.size();
    }

    // Helper methods
    private NavigableMap<Double, PriceLevel> getBookForSide(OrderSide side) {
        return side == OrderSide.BUY ? bids : asks;
    }

    // Getters
    public String getSymbol() { return symbol; }

    @Override
    public String toString() {
        return String.format("OrderBook{symbol='%s', bestBid=%.2f, bestAsk=%.2f, " +
                           "spread=%.4f, orderCount=%d}",
                           symbol, getBestBid(), getBestAsk(), getSpread(), getOrderCount());
    }
}
