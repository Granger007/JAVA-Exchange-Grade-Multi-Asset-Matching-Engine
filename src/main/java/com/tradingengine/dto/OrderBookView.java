package com.tradingengine.dto;

import java.util.List;

/**
 * ORDER BOOK VIEW - Data Transfer Object for order book representation
 * 
 * GOOD DESIGN: Clean separation between domain model and API representation
 * MVC: View layer DTO - Only for presentation purposes
 */
public class OrderBookView {
    private final String symbol;
    private final List<PriceLevelView> bids;
    private final List<PriceLevelView> asks;
    
    public OrderBookView(String symbol, List<PriceLevelView> bids, List<PriceLevelView> asks) {
        this.symbol = symbol;
        this.bids = bids;
        this.asks = asks;
    }
    
    public String getSymbol() { return symbol; }
    public List<PriceLevelView> getBids() { return bids; }
    public List<PriceLevelView> getAsks() { return asks; }
    
    /**
     * Price level view for API representation
     */
    public static class PriceLevelView {
        private final double price;
        private final long totalQuantity;
        private final int orderCount;
        
        public PriceLevelView(double price, long totalQuantity, int orderCount) {
            this.price = price;
            this.totalQuantity = totalQuantity;
            this.orderCount = orderCount;
        }
        
        public double getPrice() { return price; }
        public long getTotalQuantity() { return totalQuantity; }
        public int getOrderCount() { return orderCount; }
    }
}
