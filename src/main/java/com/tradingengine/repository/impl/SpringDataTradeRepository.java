package com.tradingengine.repository.impl;

import com.tradingengine.domain.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SpringDataTradeRepository extends JpaRepository<Trade, String> {
    
    @Query("SELECT t FROM Trade t WHERE t.buyOrderId = :orderId OR t.sellOrderId = :orderId")
    List<Trade> findByOrderId(@Param("orderId") String orderId);
    
    @Query("SELECT t FROM Trade t WHERE t.buyTraderId = :traderId OR t.sellTraderId = :traderId")
    List<Trade> findByTraderId(@Param("traderId") String traderId);
    
    List<Trade> findBySymbol(String symbol);
    
    @Query("SELECT t FROM Trade t WHERE t.symbol = :symbol AND t.executedAt BETWEEN :startTime AND :endTime")
    List<Trade> findBySymbolAndTimeRange(@Param("symbol") String symbol, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    long countBySymbol(String symbol);
    
    @Query("SELECT COALESCE(SUM(t.notionalValue), 0) FROM Trade t WHERE t.symbol = :symbol")
    double getTotalVolumeBySymbol(@Param("symbol") String symbol);
}
