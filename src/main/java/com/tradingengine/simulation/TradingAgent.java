package com.tradingengine.simulation;

import com.tradingengine.service.OrderService;

import java.util.Random;
import java.util.UUID;

/**
 * TRADING AGENT - Base class for autonomous trading agents
 * 
 * GOOD DESIGN: Clean agent abstraction with strategy pattern
 * SOLID: SRP - Each agent handles only trading logic
 * GRASP: Information Expert - Agent owns trading strategy
 */
public abstract class TradingAgent {
    
    protected final String agentId;
    protected final String name;
    protected final String symbol;
    protected final OrderService orderService;
    protected final Random random;
    protected boolean active;
    
    public TradingAgent(String name, String symbol, OrderService orderService) {
        this.agentId = UUID.randomUUID().toString();
        this.name = name;
        this.symbol = symbol;
        this.orderService = orderService;
        this.random = new Random();
        this.active = true;
    }
    
    /**
     * Execute trading strategy - to be implemented by specific agents
     */
    public abstract void executeStrategy();
    
    /**
     * Place a buy order
     */
    protected void placeBuyOrder(double price, long quantity) {
        try {
            var orderRequest = new com.tradingengine.dto.OrderRequest();
            orderRequest.setSymbol(symbol);
            orderRequest.setSide("BUY");
            orderRequest.setPrice(price);
            orderRequest.setQuantity(quantity);
            orderRequest.setTraderId(agentId);
            
            orderService.placeOrder(orderRequest);
            System.out.println(name + " placed BUY order: " + quantity + " @ " + price);
        } catch (Exception e) {
            System.err.println(name + " failed to place BUY order: " + e.getMessage());
        }
    }
    
    /**
     * Place a sell order
     */
    protected void placeSellOrder(double price, long quantity) {
        try {
            var orderRequest = new com.tradingengine.dto.OrderRequest();
            orderRequest.setSymbol(symbol);
            orderRequest.setSide("SELL");
            orderRequest.setPrice(price);
            orderRequest.setQuantity(quantity);
            orderRequest.setTraderId(agentId);
            
            orderService.placeOrder(orderRequest);
            System.out.println(name + " placed SELL order: " + quantity + " @ " + price);
        } catch (Exception e) {
            System.err.println(name + " failed to place SELL order: " + e.getMessage());
        }
    }
    
    /**
     * Get current market price (simplified)
     */
    protected abstract double getCurrentMarketPrice();
    
    /**
     * Generate random order quantity
     */
    protected long generateQuantity(long min, long max) {
        if (min >= max) return min;
        return min + Math.abs(random.nextLong()) % (max - min + 1);
    }
    
    /**
     * Generate random price around market price
     */
    protected double generatePrice(double marketPrice, double spread) {
        double deviation = (random.nextDouble() - 0.5) * spread;
        return marketPrice + deviation;
    }
    
    // Getters
    public String getAgentId() { return agentId; }
    public String getName() { return name; }
    public String getSymbol() { return symbol; }
    public boolean isActive() { return active; }
    
    public void setActive(boolean active) { this.active = active; }
    
    @Override
    public String toString() {
        return String.format("TradingAgent{id='%s', name='%s', symbol='%s', active=%s}", 
                           agentId, name, symbol, active);
    }
}
