package com.tradingengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * TRADING ENGINE APPLICATION - Main Spring Boot application
 * 
 * GOOD DESIGN: Clean application entry point with proper component scanning
 * Scans the new com.tradingengine package for production components
 * Bad design packages are excluded from scanning for safety
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.tradingengine")
public class TradingEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingEngineApplication.class, args);
        System.out.println("=".repeat(80));
        System.out.println("PRODUCTION-GRADE MULTI-ASSET TRADING ENGINE");
        System.out.println("=".repeat(80));
        System.out.println("Application started successfully!");
        System.out.println("Access the REST API at: http://localhost:8080");
        System.out.println("API Endpoints:");
        System.out.println("  POST /api/v1/trading/orders - Place order");
        System.out.println("  GET  /api/v1/trading/orders/{id} - Get order");
        System.out.println("  GET  /api/v1/trading/orders/trader/{id} - Get trader orders");
        System.out.println("  GET  /api/v1/trading/orders/active/{symbol} - Get active orders");
        System.out.println("=".repeat(80));
        System.out.println("NOTE: Bad design examples are isolated and safe.");
        System.out.println("Run BadDesignDemoRunner manually to see anti-patterns.");
        System.out.println("=".repeat(80));
    }
}
