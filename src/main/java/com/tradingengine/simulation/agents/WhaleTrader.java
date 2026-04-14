package com.tradingengine.simulation.agents;

import com.tradingengine.service.OrderService;
import com.tradingengine.simulation.TradingAgent;

/**
 * WHALE TRADER - Large volume trading agent
 * 
 * GOOD DESIGN: High-volume trading strategy
 * Places large orders that can significantly impact the market
 */
public class WhaleTrader extends TradingAgent {
    
    private final double basePrice;
    private final double maxSpread;
    private final long maxQuantity;
    
    public WhaleTrader(String name, String symbol, double basePrice, OrderService orderService) {
        super(name, symbol, orderService);
        this.basePrice = basePrice;
        this.maxSpread = basePrice * 0.01; // 1% spread (tighter for whales)
        this.maxQuantity = 10000; // Large quantities
    }
    
    @Override
    public void executeStrategy() {
        if (!active) return;
        
        // Whales trade less frequently but with larger volumes
        if (random.nextDouble() < 0.05) { // 5% chance to trade
            boolean buy = random.nextBoolean();
            
            if (buy) {
                double price = generatePrice(basePrice, maxSpread * 0.3); // Tighter spread
                long quantity = generateQuantity(1000, maxQuantity);
                placeBuyOrder(price, quantity);
                System.out.println(name + " (WHALE) placed LARGE BUY order: " + quantity + " @ " + price);
            } else {
                double price = generatePrice(basePrice, maxSpread * 0.3); // Tighter spread
                long quantity = generateQuantity(1000, maxQuantity);
                placeSellOrder(price, quantity);
                System.out.println(name + " (WHALE) placed LARGE SELL order: " + quantity + " @ " + price);
            }
        }
    }
    
    @Override
    protected double getCurrentMarketPrice() {
        return basePrice;
    }
}
