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
        if (random.nextDouble() < 0.1) { // 10% chance to trade
            boolean buy = random.nextBoolean();
            boolean aggressive = random.nextDouble() < 0.45; // 45% chance of aggressive order that will match
            
            if (buy) {
                double price;
                if (aggressive) {
                    // VERY Aggressive: Buy at a price way above base to guarantee matching asks
                    price = basePrice + (maxSpread * 1.0);
                } else {
                    // Passive: Buy below base price to add to bids
                    price = basePrice - (random.nextDouble() * maxSpread * 0.45);
                }
                long quantity = generateQuantity(1000, maxQuantity);
                placeBuyOrder(price, quantity);
                System.out.println(name + " (WHALE) placed LARGE BUY order: " + quantity + " @ " + price);
            } else {
                double price;
                if (aggressive) {
                    // VERY Aggressive: Sell at a price way below base to guarantee matching bids
                    price = basePrice - (maxSpread * 1.0);
                } else {
                    // Passive: Sell above base price to add to asks
                    price = basePrice + (random.nextDouble() * maxSpread * 0.45);
                }
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
