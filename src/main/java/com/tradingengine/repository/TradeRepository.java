package com.tradingengine.repository;

import com.tradingengine.domain.model.Trade;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * TRADE REPOSITORY - Abstraction for trade persistence
 * 
 * GOOD DESIGN: Clean repository interface, domain-focused queries
 * DDD: Repository pattern for trade persistence
 * SOLID: DIP - Service depends on abstraction
 */
public interface TradeRepository {
    
    /**
     * Save trade (create or update)
     */
    Trade save(Trade trade);
    
    /**
     * Find trade by ID
     */
    Optional<Trade> findById(String tradeId);
    
    /**
     * Find trades by order ID
     */
    List<Trade> findByOrderId(String orderId);
    
    /**
     * Find trades for a trader
     */
    List<Trade> findByTraderId(String traderId);
    
    /**
     * Find trades by symbol
     */
    List<Trade> findBySymbol(String symbol);
    
    /**
     * Find trades by symbol in time range
     */
    List<Trade> findBySymbolAndTimeRange(String symbol, Instant startTime, Instant endTime);
    
    /**
     * Find recent trades for symbol
     */
    List<Trade> findRecentTrades(String symbol, int limit);
    
    /**
     * Count trades by symbol
     */
    long countBySymbol(String symbol);
    
    /**
     * Get total volume by symbol
     */
    double getTotalVolumeBySymbol(String symbol);
    
    /**
     * Delete trade by ID
     */
    boolean deleteById(String tradeId);

    /**
     * Delete all trades
     */
    void deleteAll();
}
