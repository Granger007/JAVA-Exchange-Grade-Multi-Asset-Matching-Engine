package com.tradingengine.domain.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PORTFOLIO - Tracks user balances and positions
 */
public class Portfolio {
    private final String traderId;
    private double balance;
    private final Map<String, Long> positions; // symbol -> quantity
    
    public Portfolio(String traderId, double initialBalance) {
        this.traderId = traderId;
        this.balance = initialBalance;
        this.positions = new ConcurrentHashMap<>();
    }
    
    public void updateFromTrade(Trade trade) {
        boolean isBuyer = trade.getBuyTraderId().equals(traderId);
        boolean isSeller = trade.getSellTraderId().equals(traderId);
        
        double cost = trade.getPrice() * trade.getQuantity();
        
        if (isBuyer) {
            this.balance -= cost;
            this.positions.merge(trade.getSymbol(), trade.getQuantity(), Long::sum);
        } else if (isSeller) {
            this.balance += cost;
            long currentPos = this.positions.getOrDefault(trade.getSymbol(), 0L);
            long newPos = currentPos - trade.getQuantity();
            if (newPos == 0) {
                this.positions.remove(trade.getSymbol());
            } else {
                this.positions.put(trade.getSymbol(), newPos);
            }
        }
    }
    
    public String getTraderId() { return traderId; }
    public double getBalance() { return balance; }
    public Map<String, Long> getPositions() { return positions; }
}
