package com.tradingengine.bad_design.tight_coupling;

import java.util.*;

/**
 * TIGHTLY COUPLED CONTROLLER - BAD DESIGN EXAMPLE
 * 
 * NOTE: This class intentionally contains unused fields and poor design patterns
 * for educational purposes. The warnings about unused fields are DELIBERATE
 * to demonstrate anti-patterns that should be avoided in production code.
 * 
 * VIOLATES:
 * - Dependency Inversion Principle (DIP) - Direct instantiation
 * - Controller GRASP - Business logic in controller
 * - Single Responsibility Principle (SRP) - Multiple responsibilities
 * 
 * PROBLEMS:
 * - Direct instantiation creates tight coupling
 * - Cannot inject mocks for testing
 * - Business logic mixed with HTTP concerns
 * - Hard to extend and maintain
 * - Unused fields demonstrate poor resource management
 * 
 * NEVER USE IN PRODUCTION - FOR EDUCATION ONLY
 */
public class TightlyCoupledController {
    
    // BAD: Direct instantiation - tight coupling to concrete classes
    private OrderBookManager orderBookManager = new OrderBookManager();
    private TradeService tradeService = new TradeService();
    private UserService userService = new UserService();
    private NotificationService notificationService = new NotificationService();
    private ValidationService validationService = new ValidationService(); // BAD: Unused field - demonstrates poor design (anti-pattern)
    
    /**
     * BAD: Controller doing everything - violates SRP and GRASP Controller
     * - HTTP handling
     * - Business logic
     * - Validation
     * - Persistence
     * - Notifications
     */
    public Map<String, Object> placeOrder(Map<String, Object> request) {
        
        // BAD: Validation logic in controller
        if (request.get("symbol") == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Symbol is required");
            return error;
        }
        if (request.get("price") == null || (Double) request.get("price") <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid price");
            return error;
        }
        if (request.get("quantity") == null || (Long) request.get("quantity") <= 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid quantity");
            return error;
        }
        
        // BAD: Business logic in controller
        String symbol = (String) request.get("symbol");
        double price = (Double) request.get("price");
        long quantity = (Long) request.get("quantity");
        String side = (String) request.get("side");
        String traderId = (String) request.get("traderId");
        
        // BAD: Direct user validation mixed in
        if (!userService.isValidTrader(traderId)) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid trader");
            return error;
        }
        
        // BAD: Order creation logic in controller
        Order order = new Order();
        order.orderId = UUID.randomUUID().toString();
        order.symbol = symbol;
        order.price = price;
        order.quantity = quantity;
        order.side = side;
        order.traderId = traderId;
        order.status = "NEW";
        order.createdAt = new Date();
        
        // BAD: Direct business logic execution
        List<Trade> trades = orderBookManager.processOrder(order);
        
        // BAD: Direct persistence calls
        try {
            tradeService.saveTrades(trades);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to save trades: " + e.getMessage());
            return error;
        }
        
        // BAD: Direct notification logic
        for (Trade trade : trades) {
            try {
                notificationService.sendTradeNotification(trade.buyTraderId, trade);
                notificationService.sendTradeNotification(trade.sellTraderId, trade);
            } catch (Exception e) {
                // BAD: Error handling mixed in
                System.err.println("Failed to send notification: " + e.getMessage());
            }
        }
        
        // BAD: Response construction mixed with business logic
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", order.orderId);
        response.put("status", order.status);
        response.put("trades", trades);
        response.put("timestamp", new Date());
        
        return response;
    }
    
    /**
     * BAD: More business logic in controller
     */
    public Map<String, Object> cancelOrder(String orderId) {
        // BAD: Validation mixed in
        if (orderId == null || orderId.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Order ID is required");
            return error;
        }
        
        // BAD: Direct business logic calls
        boolean cancelled = orderBookManager.cancelOrder(orderId);
        
        // BAD: Response logic mixed in
        Map<String, Object> response = new HashMap<>();
        response.put("cancelled", cancelled);
        response.put("orderId", orderId);
        response.put("timestamp", new Date());
        
        return response;
    }
    
    /**
     * BAD: Even more business logic in controller
     */
    public Map<String, Object> getOrderBook(String symbol) {
        // BAD: Validation mixed in
        if (symbol == null || symbol.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Symbol is required");
            return error;
        }
        
        // BAD: Direct data access
        OrderBook orderBook = orderBookManager.getOrderBook(symbol);
        
        // BAD: Data transformation in controller
        Map<String, Object> response = new HashMap<>();
        response.put("symbol", symbol);
        response.put("bids", orderBook.bids);
        response.put("asks", orderBook.asks);
        response.put("timestamp", new Date());
        
        return response;
    }
    
    // BAD: All dependencies are concrete classes - no interfaces
    public static class OrderBookManager {
        public List<Trade> processOrder(Order order) {
            // Business logic
            return new ArrayList<>();
        }
        public boolean cancelOrder(String orderId) {
            // Business logic
            return true;
        }
        public OrderBook getOrderBook(String symbol) {
            // Business logic
            return new OrderBook();
        }
    }
    
    public static class TradeService {
        public void saveTrades(List<Trade> trades) {
            // Persistence logic
        }
    }
    
    public static class UserService {
        public boolean isValidTrader(String traderId) {
            // Validation logic
            return true;
        }
    }
    
    public static class NotificationService {
        public void sendTradeNotification(String traderId, Trade trade) {
            // Notification logic
        }
    }
    
    public static class ValidationService {
        // Validation logic
    }
    
    // BAD: Data structures mixed in
    public static class Order {
        public String orderId;
        public String symbol;
        public double price;
        public long quantity;
        public String side;
        public String traderId;
        public String status;
        public Date createdAt;
    }
    
    public static class Trade {
        public String tradeId;
        public String buyTraderId;
        public String sellTraderId;
        public String symbol;
        public double price;
        public long quantity;
    }
    
    public static class OrderBook {
        public Map<Double, List<Order>> bids = new HashMap<>();
        public Map<Double, List<Order>> asks = new HashMap<>();
    }
}
