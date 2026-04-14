package com.tradingengine.strategy;

import com.tradingengine.domain.model.Order;
import com.tradingengine.domain.model.OrderBook;
import com.tradingengine.domain.model.Trade;

import java.util.List;

/**
 * MATCHINGSTRATEGY - Strategy interface for order matching algorithms
 * 
 * GOOD DESIGN: Strategy pattern enables OCP - add new algorithms without modifying core engine
 * SOLID: OCP - Open for extension, closed for modification
 * DIP - High-level modules depend on abstraction, not concrete implementations
 */
public interface MatchingStrategy {
    
    /**
     * Match incoming order against order book and generate trades
     * 
     * @param incomingOrder New order to match
     * @param orderBook Current order book state
     * @return List of executed trades (may be empty if no matches)
     */
    List<Trade> match(Order incomingOrder, OrderBook orderBook);
    
    /**
     * Get strategy name for identification
     * @return Strategy name
     */
    String getStrategyName();
    
    /**
     * Validate if strategy supports the given order type/symbol
     * @param order Order to validate
     * @return true if strategy can handle this order
     */
    default boolean supports(Order order) {
        return true; // Default implementation supports all orders
    }
}
