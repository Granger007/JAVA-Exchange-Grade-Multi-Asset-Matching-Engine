package com.tradingengine.service;

import com.tradingengine.domain.model.Portfolio;
import com.tradingengine.domain.model.Trade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PORTFOLIO SERVICE - Business logic for portfolio management
 * 
 * GOOD DESIGN: Clean separation of concerns, proper transaction management
 * MVC: Service layer handling portfolio business logic
 * GRASP: Controller - Coordinates portfolio operations
 * SOLID: SRP - Only handles portfolio-related business operations
 */
@Service
@Transactional
public class PortfolioService {
    
    private final Map<String, Portfolio> portfolios = new ConcurrentHashMap<>();
    
    /**
     * Get portfolio by trader ID
     * 
     * @param traderId Trader ID
     * @return Portfolio details
     */
    public Portfolio getPortfolio(String traderId) {
        return portfolios.computeIfAbsent(traderId, id -> new Portfolio(id, 10000.0));
    }
    
    /**
     * Process a trade and update portfolios
     * 
     * @param trade Trade to process
     */
    public void processTrade(Trade trade) {
        // Update buyer portfolio
        Portfolio buyerPortfolio = getPortfolio(trade.getBuyTraderId());
        buyerPortfolio.updateFromTrade(trade);
        
        // Update seller portfolio
        Portfolio sellerPortfolio = getPortfolio(trade.getSellTraderId());
        sellerPortfolio.updateFromTrade(trade);
    }
    
    /**
     * Clear all portfolios
     */
    public void clearAllPortfolios() {
        portfolios.clear();
    }
}