package com.tradingengine.service;

import com.tradingengine.domain.model.Portfolio;
import com.tradingengine.domain.model.Trade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PortfolioService {
    
    private final Map<String, Portfolio> portfolios = new ConcurrentHashMap<>();
    
    public Portfolio getPortfolio(String traderId) {
        return portfolios.computeIfAbsent(traderId, id -> new Portfolio(id, 100000.0)); // Default balance 100k
    }

    public List<Portfolio> getAllPortfolios() {
        return List.copyOf(portfolios.values());
    }
    
    public void processTrade(Trade trade) {
        Portfolio buyerPortfolio = getPortfolio(trade.getBuyTraderId());
        Portfolio sellerPortfolio = getPortfolio(trade.getSellTraderId());
        
        buyerPortfolio.updateFromTrade(trade);
        if (!trade.getBuyTraderId().equals(trade.getSellTraderId())) {
            sellerPortfolio.updateFromTrade(trade);
        }
    }

    public void clearAllPortfolios() {
        portfolios.clear();
    }
}
