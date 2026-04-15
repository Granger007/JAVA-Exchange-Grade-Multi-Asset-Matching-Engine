package com.tradingengine.simulation.agents;

import com.tradingengine.service.OrderService;
import com.tradingengine.simulation.TradingAgent;

/**
 * RETAIL TRADER - Simple retail trading agent
 * 
 * GOOD DESIGN: Specific agent implementation with clear strategy
 * Places small orders with random timing and pricing
 */
public class RetailTrader extends TradingAgent {
    
    private final double basePrice;
    private final double maxSpread;
    private final long maxQuantity;
    
    public RetailTrader(String name, String symbol, double basePrice, OrderService orderService) {
        super(name, symbol, orderService);
        this.basePrice = basePrice;
        this.maxSpread = basePrice * 0.02; // 2% spread
        this.maxQuantity = 100;
    }
    
    @Override
    public void executeStrategy() {
        if (!active) return;
        
        // Retail traders trade infrequently
        if (random.nextDouble() < 0.2) { // 20% chance to trade
            boolean buy = random.nextBoolean();
            boolean aggressive = random.nextDouble() < 0.55; // 55% chance of aggressive order that will match
            
            if (buy) {
                double price;
                if (aggressive) {
                    // VERY Aggressive: Buy at a price way above base to guarantee matching asks
                    price = basePrice + (maxSpread * 1.0);
                } else {
                    // Passive: Buy below base price to add to bids
                    price = basePrice - (random.nextDouble() * maxSpread * 0.5);
                }
                long quantity = generateQuantity(10, maxQuantity);
                placeBuyOrder(price, quantity);
            } else {
                double price;
                if (aggressive) {
                    // VERY Aggressive: Sell at a price way below base to guarantee matching bids
                    price = basePrice - (maxSpread * 1.0);
                } else {
                    // Passive: Sell above base price to add to asks
                    price = basePrice + (random.nextDouble() * maxSpread * 0.5);
                }
                long quantity = generateQuantity(10, maxQuantity);
                placeSellOrder(price, quantity);
            }
        }
    }
    
    @Override
    protected double getCurrentMarketPrice() {
        return basePrice;
    }
}
