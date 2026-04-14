package com.tradingengine.repository.impl;

import com.tradingengine.domain.model.Trade;
import com.tradingengine.repository.TradeRepository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IN-MEMORY TRADE REPOSITORY - Simple implementation for demonstration
 * 
 * GOOD DESIGN: Implements repository interface, clean separation
 * In production, this would be replaced with JPA/JDBC implementation
 */
public class InMemoryTradeRepository implements TradeRepository {
    
    private final Map<String, Trade> trades = new ConcurrentHashMap<>();
    
    @Override
    public Trade save(Trade trade) {
        trades.put(trade.getTradeId(), trade);
        return trade;
    }
    
    @Override
    public Optional<Trade> findById(String tradeId) {
        return Optional.ofNullable(trades.get(tradeId));
    }
    
    @Override
    public List<Trade> findByOrderId(String orderId) {
        return trades.values().stream()
                .filter(trade -> trade.getBuyOrderId().equals(orderId) || 
                               trade.getSellOrderId().equals(orderId))
                .toList();
    }
    
    @Override
    public List<Trade> findByTraderId(String traderId) {
        return trades.values().stream()
                .filter(trade -> trade.getBuyTraderId().equals(traderId) || 
                               trade.getSellTraderId().equals(traderId))
                .toList();
    }
    
    @Override
    public List<Trade> findBySymbol(String symbol) {
        return trades.values().stream()
                .filter(trade -> trade.getSymbol().equals(symbol))
                .toList();
    }
    
    @Override
    public List<Trade> findBySymbolAndTimeRange(String symbol, Instant startTime, Instant endTime) {
        return trades.values().stream()
                .filter(trade -> trade.getSymbol().equals(symbol))
                .filter(trade -> !trade.getExecutedAt().isBefore(startTime) && 
                               !trade.getExecutedAt().isAfter(endTime))
                .toList();
    }
    
    @Override
    public List<Trade> findRecentTrades(String symbol, int limit) {
        return trades.values().stream()
                .filter(trade -> trade.getSymbol().equals(symbol))
                .sorted((t1, t2) -> t2.getExecutedAt().compareTo(t1.getExecutedAt()))
                .limit(limit)
                .toList();
    }
    
    @Override
    public long countBySymbol(String symbol) {
        return trades.values().stream()
                .filter(trade -> trade.getSymbol().equals(symbol))
                .count();
    }
    
    @Override
    public double getTotalVolumeBySymbol(String symbol) {
        return trades.values().stream()
                .filter(trade -> trade.getSymbol().equals(symbol))
                .mapToDouble(Trade::getNotionalValue)
                .sum();
    }
    
    @Override
    public boolean deleteById(String tradeId) {
        return trades.remove(tradeId) != null;
    }

    @Override
    public void deleteAll() {
        trades.clear();
    }
}
