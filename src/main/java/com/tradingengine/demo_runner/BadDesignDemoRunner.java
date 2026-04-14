package com.tradingengine.demo_runner;

import com.tradingengine.bad_design.god_objects.GodMatchingEngine;
import com.tradingengine.bad_design.tight_coupling.TightlyCoupledController;
import com.tradingengine.bad_design.encapsulation_violation.UnsafeOrderBook;

import java.util.HashMap;
import java.util.Map;

/**
 * BAD DESIGN DEMO RUNNER - Educational Tool
 * 
 * PURPOSE:
 * - Demonstrate bad design principles
 * - Show problems in practice
 * - Compare with good design
 * 
 * USAGE:
 * - Run manually to see bad design issues
 * - NEVER use in production code
 * - For educational purposes only
 * 
 * This class is isolated and safe to run without affecting production code.
 */
public class BadDesignDemoRunner {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("BAD DESIGN DEMONSTRATIONS - EDUCATIONAL PURPOSES ONLY");
        System.out.println("=".repeat(80));
        System.out.println();
        
        demonstrateGodObject();
        demonstrateTightCoupling();
        demonstrateEncapsulationViolation();
        
        System.out.println("=".repeat(80));
        System.out.println("BAD DESIGN DEMOS COMPLETED");
        System.out.println("See the good design implementations in the main packages");
        System.out.println("for proper SOLID and GRASP principles application.");
        System.out.println("=".repeat(80));
    }
    
    /**
     * Demonstrates God Object anti-pattern
     */
    private static void demonstrateGodObject() {
        System.out.println("1. GOD OBJECT ANTI-PATTERN DEMONSTRATION");
        System.out.println("-".repeat(50));
        
        System.out.println("PROBLEMS WITH GOD OBJECT:");
        System.out.println("  - Single Responsibility Principle VIOLATION");
        System.out.println("  - Open/Closed Principle VIOLATION");
        System.out.println("  - Dependency Inversion Principle VIOLATION");
        System.out.println("  - Hard to test due to tight coupling");
        System.out.println("  - Cannot extend without modification");
        System.out.println();
        
        // BAD: Direct instantiation of God object
        GodMatchingEngine godEngine = new GodMatchingEngine();
        
        System.out.println("Creating and processing order with GodMatchingEngine...");
        
        // BAD: God object does everything
        var trades = godEngine.processOrder("AAPL", 150.25, 100, "BUY", "TRADER001");
        
        System.out.println("Result: " + trades.size() + " trades executed");
        System.out.println("ISSUE: One class handles matching, persistence, notifications, logging...");
        System.out.println();
    }
    
    /**
     * Demonstrates Tight Coupling anti-pattern
     */
    private static void demonstrateTightCoupling() {
        System.out.println("2. TIGHT COUPLING ANTI-PATTERN DEMONSTRATION");
        System.out.println("-".repeat(50));
        
        System.out.println("PROBLEMS WITH TIGHT COUPLING:");
        System.out.println("  - Dependency Inversion Principle VIOLATION");
        System.out.println("  - Controller GRASP Principle VIOLATION");
        System.out.println("  - Cannot inject mocks for testing");
        System.out.println("  - Business logic mixed with HTTP concerns");
        System.out.println();
        
        // BAD: Direct instantiation creates tight coupling
        TightlyCoupledController controller = new TightlyCoupledController();
        
        System.out.println("Processing order request with TightlyCoupledController...");
        
        // BAD: Controller doing everything
        Map<String, Object> request = new HashMap<>();
        request.put("symbol", "AAPL");
        request.put("price", 150.25);
        request.put("quantity", 100);
        request.put("side", "BUY");
        request.put("traderId", "TRADER001");
        
        var response = controller.placeOrder(request);
        
        System.out.println("Result: " + response);
        System.out.println("ISSUE: Controller directly instantiates dependencies, mixes concerns...");
        System.out.println();
    }
    
    /**
     * Demonstrates Encapsulation Violation anti-pattern
     */
    private static void demonstrateEncapsulationViolation() {
        System.out.println("3. ENCAPSULATION VIOLATION ANTI-PATTERN DEMONSTRATION");
        System.out.println("-".repeat(50));
        
        System.out.println("PROBLEMS WITH ENCAPSULATION VIOLATION:");
        System.out.println("  - Information Hiding Principle VIOLATION");
        System.out.println("  - External code can bypass invariants");
        System.out.println("  - Cannot maintain data integrity");
        System.out.println("  - Thread safety issues");
        System.out.println();
        
        // BAD: Direct access to internal state
        UnsafeOrderBook unsafeBook = new UnsafeOrderBook("AAPL");
        
        System.out.println("Creating UnsafeOrderBook and demonstrating violations...");
        
        // BAD: Direct exposure of internal collections
        System.out.println("Direct access to internal bids: " + unsafeBook.getBids().size());
        
        // BAD: Can modify internal state directly
        unsafeBook.setTotalVolume(-1000);
        System.out.println("Set invalid volume: " + unsafeBook.getTotalVolume());
        
        // BAD: Can change symbol after creation
        unsafeBook.setSymbol("MANIPULATED");
        System.out.println("Changed symbol to: " + unsafeBook.symbol);
        
        // BAD: Can wipe all data directly
        unsafeBook.clearAllOrders();
        System.out.println("Cleared all orders directly!");
        
        System.out.println("ISSUE: External code can corrupt internal state at will...");
        System.out.println();
        
        // Demonstrate the built-in violation method
        System.out.println("Running built-in encapsulation violation demo...");
        UnsafeOrderBook.demonstrateEncapsulationViolation();
        System.out.println();
    }
    
    }
