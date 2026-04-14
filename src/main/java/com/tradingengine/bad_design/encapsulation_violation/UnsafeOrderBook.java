package com.tradingengine.bad_design.encapsulation_violation;

import java.util.*;

/**
 * UNSAFE ORDER BOOK - BAD DESIGN EXAMPLE
 * 
 * VIOLATES:
 * - Encapsulation Principle - Exposes internal state
 * - Information Hiding - Direct access to internal structures
 * - Immutability - Allows external mutation
 * 
 * PROBLEMS:
 * - Direct exposure of internal collections
 * - External code can bypass invariants
 * - Cannot maintain data integrity
 * - Thread safety issues
 * 
 * NEVER USE IN PRODUCTION - FOR EDUCATION ONLY
 */
public class UnsafeOrderBook {
    
    // BAD: Direct exposure of internal mutable collections
    public TreeMap<Double, List<Order>> bids;
    public TreeMap<Double, List<Order>> asks;
    public Map<String, Order> orderMap;
    
    // BAD: No encapsulation of internal state
    public String symbol;
    public long totalVolume;
    public int orderCount;
    
    public UnsafeOrderBook(String symbol) {
        this.symbol = symbol;
        // BAD: Direct exposure of mutable collections
        this.bids = new TreeMap<>(Collections.reverseOrder());
        this.asks = new TreeMap<>();
        this.orderMap = new HashMap<>();
        this.totalVolume = 0;
        this.orderCount = 0;
    }
    
    /**
     * BAD: Direct exposure of internal state
     * Allows external code to modify internal structure directly
     */
    public TreeMap<Double, List<Order>> getBids() {
        return bids; // DANGEROUS: Direct reference to mutable collection
    }
    
    /**
     * BAD: Direct exposure of internal state
     */
    public TreeMap<Double, List<Order>> getAsks() {
        return asks; // DANGEROUS: Direct reference to mutable collection
    }
    
    /**
     * BAD: Direct exposure of internal state
     */
    public Map<String, Order> getOrderMap() {
        return orderMap; // DANGEROUS: Direct reference to mutable collection
    }
    
    /**
     * BAD: No validation on order addition
     * Allows invalid orders to be added
     */
    public void addOrder(Order order) {
        // BAD: No validation of order state
        // BAD: No symbol checking
        // BAD: No null checks
        
        TreeMap<Double, List<Order>> book = order.side.equals("BUY") ? bids : asks;
        book.computeIfAbsent(order.price, k -> new ArrayList<>()).add(order);
        orderMap.put(order.orderId, order);
        orderCount++;
        
        // BAD: State modification without proper synchronization
        totalVolume += order.price * order.quantity;
    }
    
    /**
     * BAD: Unsafe order removal
     */
    public boolean removeOrder(String orderId) {
        Order order = orderMap.get(orderId);
        if (order == null) {
            return false;
        }
        
        // BAD: No validation
        TreeMap<Double, List<Order>> book = order.side.equals("BUY") ? bids : asks;
        List<Order> ordersAtPrice = book.get(order.price);
        
        if (ordersAtPrice != null) {
            ordersAtPrice.remove(order);
            if (ordersAtPrice.isEmpty()) {
                book.remove(order.price);
            }
        }
        
        orderMap.remove(orderId);
        orderCount--;
        totalVolume -= order.price * order.quantity;
        
        return true;
    }
    
    /**
     * BAD: Direct field access - no encapsulation
     */
    public long getTotalVolume() {
        return totalVolume; // BAD: Direct field access
    }
    
    /**
     * BAD: Direct field modification allowed
     */
    public void setTotalVolume(long totalVolume) {
        this.totalVolume = totalVolume; // DANGEROUS: External code can modify state
    }
    
    /**
     * BAD: Public fields that can be modified directly
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol; // DANGEROUS: Symbol can be changed after creation
    }
    
    /**
     * BAD: Method that exposes internal implementation details
     */
    public void clearAllOrders() {
        // BAD: Allows external code to wipe all data
        bids.clear();
        asks.clear();
        orderMap.clear();
        orderCount = 0;
        totalVolume = 0;
    }
    
    /**
     * BAD: Direct manipulation of internal structure
     */
    public void manipulateOrdersDirectly() {
        // BAD: External code can directly manipulate internal state
        for (List<Order> orders : bids.values()) {
            for (Order order : orders) {
                // DANGEROUS: Direct modification of order state
                order.price = order.price * 1.1; // Manipulate prices
                order.quantity = order.quantity / 2; // Manipulate quantities
            }
        }
    }
    
    /**
     * BAD: No thread safety - concurrent access issues
     */
    public void unsafeConcurrentAccess() {
        // BAD: No synchronization for concurrent access
        // Multiple threads can corrupt internal state
        
        // Example of race condition:
        // Thread 1: addOrder() -> orderCount++
        // Thread 2: addOrder() -> orderCount++
        // Thread 3: removeOrder() -> orderCount--
        // Result: orderCount may be incorrect
    }
    
    // BAD: Inner classes with public fields - no encapsulation
    public static class Order {
        // BAD: All fields are public and mutable
        public String orderId;
        public String symbol;
        public double price;
        public long quantity;
        public String side;
        public String traderId;
        public String status;
        public Date timestamp;
        
        // BAD: No validation in constructor
        public Order() {
            this.timestamp = new Date();
        }
        
        // BAD: No encapsulation - direct field access
        public void setPrice(double price) {
            this.price = price; // No validation
        }
        
        public void setQuantity(long quantity) {
            this.quantity = quantity; // No validation
        }
        
        // BAD: Public mutable fields can be changed from anywhere
        public void manipulateOrder() {
            this.price = 0.01; // Manipulate to invalid price
            this.quantity = -1; // Manipulate to invalid quantity
            this.status = "MANIPULATED";
        }
    }
    
    /**
     * BAD: Method that demonstrates how external code can abuse the lack of encapsulation
     */
    public static void demonstrateEncapsulationViolation() {
        UnsafeOrderBook orderBook = new UnsafeOrderBook("AAPL");
        
        // BAD: Direct access to internal collections
        TreeMap<Double, List<Order>> bids = orderBook.getBids();
        bids.clear(); // Can wipe all bids directly
        
        // BAD: Direct modification of internal state
        orderBook.setTotalVolume(-1000); // Set invalid volume
        orderBook.setSymbol("MANIPULATED"); // Change symbol
        
        // BAD: Direct manipulation of orders
        Order maliciousOrder = new Order();
        maliciousOrder.orderId = "MALICIOUS";
        maliciousOrder.price = -1.0; // Invalid price
        maliciousOrder.quantity = -1; // Invalid quantity
        maliciousOrder.symbol = "FAKE";
        
        orderBook.addOrder(maliciousOrder); // No validation prevents this
        
        // BAD: Can directly modify orders in the book
        List<Order> ordersAtPrice = orderBook.bids.get(100.0);
        if (ordersAtPrice != null) {
            for (Order order : ordersAtPrice) {
                order.manipulateOrder(); // Directly manipulate order state
            }
        }
        
        System.out.println("Encapsulation violated! Order book is now corrupted.");
    }
}
