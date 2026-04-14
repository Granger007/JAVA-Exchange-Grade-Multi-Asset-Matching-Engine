package com.tradingengine.bad_design.god_objects;

import java.util.*;

/**
 * GOD MATCHING ENGINE - BAD DESIGN EXAMPLE
 * 
 * NOTE: This class intentionally contains unused fields and poor design patterns
 * for educational purposes. The warnings about unused fields are DELIBERATE
 * to demonstrate anti-patterns that should be avoided in production code.
 * 
 * VIOLATES:
 * - Single Responsibility Principle (SRP) - Does everything!
 * - Open/Closed Principle (OCP) - Hard-coded strategies
 * - Dependency Inversion Principle (DIP) - Direct dependencies
 * - GRASP Controller - Business logic mixed with infrastructure
 * 
 * PROBLEMS:
 * - Handles matching, persistence, notifications, logging
 * - Hard to test due to tight coupling
 * - Cannot extend without modifying
 * - Mixed concerns in single class
 * - Unused fields demonstrate poor resource management
 * 
 * NEVER USE IN PRODUCTION - FOR EDUCATION ONLY
 */
public class GodMatchingEngine {
    
    // BAD: Direct dependencies on concrete classes
    private Map<String, List<Order>> orders = new HashMap<>();
    private List<Trade> trades = new ArrayList<>(); // BAD: Unused field - demonstrates poor design (anti-pattern)
    private DatabaseConnection database = new DatabaseConnection();
    private EmailService emailService = new EmailService();
    private Logger logger = new Logger();
    
    // BAD: No dependency injection, direct instantiation
    public GodMatchingEngine() {
        // Direct instantiation creates tight coupling
        this.database = new DatabaseConnection();
        this.emailService = new EmailService();
    }
    
    /**
     * BAD: God method that does everything
     * - Order validation
     * - Matching logic
     * - Trade execution
     * - Persistence
     * - Notifications
     * - Logging
     */
    public List<Trade> processOrder(String symbol, double price, long quantity, 
                                   String side, String traderId) {
        
        // BAD: Validation logic mixed in
        if (price <= 0) {
            throw new IllegalArgumentException("Invalid price");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        
        // BAD: Order creation mixed with business logic
        Order order = new Order();
        order.orderId = UUID.randomUUID().toString();
        order.symbol = symbol;
        order.price = price;
        order.quantity = quantity;
        order.side = side;
        order.traderId = traderId;
        order.status = "NEW";
        
        // BAD: Hard-coded matching strategy selection
        List<Trade> executedTrades = new ArrayList<>();
        if (symbol.equals("AAPL")) {
            // Hard-coded FIFO logic
            executedTrades = matchFIFO(order);
        } else if (symbol.equals("BTC-USD")) {
            // Hard-coded Pro-Rata logic
            executedTrades = matchProRata(order);
        } else {
            // Default FIFO
            executedTrades = matchFIFO(order);
        }
        
        // BAD: Direct database access
        try {
            database.saveOrder(order);
            for (Trade trade : executedTrades) {
                database.saveTrade(trade);
            }
        } catch (Exception e) {
            // BAD: Exception handling mixed in
            logger.log("Database error: " + e.getMessage());
        }
        
        // BAD: Direct notification logic
        for (Trade trade : executedTrades) {
            try {
                emailService.sendTradeNotification(trade.buyTraderId, trade);
                emailService.sendTradeNotification(trade.sellTraderId, trade);
            } catch (Exception e) {
                logger.log("Email error: " + e.getMessage());
            }
        }
        
        // BAD: Direct logging mixed in
        logger.log("Processed order: " + order.orderId + ", trades: " + executedTrades.size());
        
        return executedTrades;
    }
    
    // BAD: Matching logic embedded in god class
    private List<Trade> matchFIFO(Order incomingOrder) {
        List<Trade> trades = new ArrayList<>();
        
        // BAD: Complex matching logic mixed in
        List<Order> oppositeOrders = orders.getOrDefault(incomingOrder.symbol, new ArrayList<>());
        
        for (Order restingOrder : oppositeOrders) {
            if (!restingOrder.side.equals(incomingOrder.side) && restingOrder.quantity > 0) {
                if (canMatch(incomingOrder, restingOrder)) {
                    Trade trade = executeTrade(incomingOrder, restingOrder);
                    trades.add(trade);
                    
                    // BAD: State modification mixed in
                    incomingOrder.quantity -= trade.quantity;
                    restingOrder.quantity -= trade.quantity;
                    
                    if (incomingOrder.quantity == 0) break;
                }
            }
        }
        
        return trades;
    }
    
    // BAD: More matching logic embedded
    private List<Trade> matchProRata(Order incomingOrder) {
        // BAD: Complex Pro-Rata logic mixed in
        List<Trade> trades = new ArrayList<>();
        // ... Pro-Rata implementation
        return trades;
    }
    
    // BAD: Helper methods mixed in
    private boolean canMatch(Order order1, Order order2) {
        if (order1.side.equals("BUY") && order2.side.equals("SELL")) {
            return order1.price >= order2.price;
        } else if (order1.side.equals("SELL") && order2.side.equals("BUY")) {
            return order1.price <= order2.price;
        }
        return false;
    }
    
    private Trade executeTrade(Order order1, Order order2) {
        // BAD: Trade creation logic mixed in
        Trade trade = new Trade();
        trade.tradeId = UUID.randomUUID().toString();
        trade.buyOrderId = order1.side.equals("BUY") ? order1.orderId : order2.orderId;
        trade.sellOrderId = order1.side.equals("SELL") ? order1.orderId : order2.orderId;
        trade.buyTraderId = order1.side.equals("BUY") ? order1.traderId : order2.traderId;
        trade.sellTraderId = order1.side.equals("SELL") ? order1.traderId : order2.traderId;
        trade.price = order2.price; // Use resting order price
        trade.quantity = Math.min(order1.quantity, order2.quantity);
        trade.symbol = order1.symbol;
        return trade;
    }
    
    // BAD: Inner classes for data structures - should be separate domain models
    public static class Order {
        public String orderId;
        public String symbol;
        public double price;
        public long quantity;
        public String side;
        public String traderId;
        public String status;
    }
    
    public static class Trade {
        public String tradeId;
        public String buyOrderId;
        public String sellOrderId;
        public String symbol;
        public double price;
        public long quantity;
        public String buyTraderId;
        public String sellTraderId;
    }
    
    // BAD: Concrete dependencies embedded
    public static class DatabaseConnection {
        public void saveOrder(Order order) {
            // Database logic
        }
        public void saveTrade(Trade trade) {
            // Database logic
        }
    }
    
    public static class EmailService {
        public void sendTradeNotification(String traderId, Trade trade) {
            // Email logic
        }
    }
    
    public static class Logger {
        public void log(String message) {
            System.out.println("LOG: " + message);
        }
    }
}
