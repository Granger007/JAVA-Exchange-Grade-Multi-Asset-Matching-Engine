package com.tradingengine.repository.impl;

import com.tradingengine.domain.model.Trade;
import com.tradingengine.repository.TradeRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaTradeRepository implements TradeRepository {

    private final SpringDataTradeRepository jpaRepository;

    public JpaTradeRepository(SpringDataTradeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Trade save(Trade trade) {
        return jpaRepository.save(trade);
    }

    @Override
    public Optional<Trade> findById(String tradeId) {
        return jpaRepository.findById(tradeId);
    }

    @Override
    public List<Trade> findByOrderId(String orderId) {
        return jpaRepository.findByOrderId(orderId);
    }

    @Override
    public List<Trade> findByTraderId(String traderId) {
        return jpaRepository.findByTraderId(traderId);
    }

    @Override
    public List<Trade> findBySymbol(String symbol) {
        return jpaRepository.findBySymbol(symbol);
    }

    @Override
    public List<Trade> findBySymbolAndTimeRange(String symbol, Instant startTime, Instant endTime) {
        return jpaRepository.findBySymbolAndTimeRange(symbol, startTime, endTime);
    }

    @Override
    public List<Trade> findRecentTrades(String symbol, int limit) {
        // Not perfectly efficient if table is huge without index, but works for repo contract
        return jpaRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "executedAt"))
        ).getContent().stream().filter(t -> t.getSymbol().equals(symbol)).toList();
    }

    @Override
    public long countBySymbol(String symbol) {
        return jpaRepository.countBySymbol(symbol);
    }

    @Override
    public double getTotalVolumeBySymbol(String symbol) {
        return jpaRepository.getTotalVolumeBySymbol(symbol);
    }

    @Override
    public boolean deleteById(String tradeId) {
        if (jpaRepository.existsById(tradeId)) {
            jpaRepository.deleteById(tradeId);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
