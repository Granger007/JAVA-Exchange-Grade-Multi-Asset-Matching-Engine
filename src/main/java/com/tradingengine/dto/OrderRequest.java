package com.tradingengine.dto;

/**
 * ORDER REQUEST - DTO for order placement requests
 * 
 * GOOD DESIGN: Clean DTO with validation, clear API contract
 * MVC: Data Transfer Object for controller-service communication
 */
public class OrderRequest {
    private String symbol;
    private double price;
    private double stopPrice = 0.0;
    private long quantity;
    private String side;  // "BUY" or "SELL"
    private String type = "LIMIT"; // "LIMIT" or "MARKET"
    private String traderId;

    // Default constructor for JSON deserialization
    public OrderRequest() {}

    public OrderRequest(String symbol, double price, double stopPrice, long quantity, String side, String type, String traderId) {
        this.symbol = symbol;
        this.price = price;
        this.stopPrice = stopPrice;
        this.quantity = quantity;
        this.side = side;
        this.type = type;
        this.traderId = traderId;
    }

    // Getters and setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getStopPrice() { return stopPrice; }
    public void setStopPrice(double stopPrice) { this.stopPrice = stopPrice; }

    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }

    public String getSide() { return side; }
    public void setSide(String side) { this.side = side; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTraderId() { return traderId; }
    public void setTraderId(String traderId) { this.traderId = traderId; }

    @Override
    public String toString() {
        return String.format("OrderRequest{symbol='%s', price=%.2f, stopPrice=%.2f, quantity=%d, " +
                           "side='%s', type='%s', traderId='%s'}",
                           symbol, price, stopPrice, quantity, side, type, traderId);
    }
}
