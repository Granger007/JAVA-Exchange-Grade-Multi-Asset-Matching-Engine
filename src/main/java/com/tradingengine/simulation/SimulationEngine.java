package com.tradingengine.simulation;

import com.tradingengine.service.OrderService;
import com.tradingengine.simulation.agents.RetailTrader;
import com.tradingengine.simulation.agents.WhaleTrader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * SIMULATION ENGINE - Manages trading agents and market simulation
 * 
 * GOOD DESIGN: Clean separation of simulation concerns
 * Manages multiple autonomous trading agents with different strategies
 */
@Component
public class SimulationEngine {
    
    private final OrderService orderService;
    private final List<TradingAgent> agents;
    private ScheduledExecutorService scheduler;
    
    @Autowired
    public SimulationEngine(OrderService orderService) {
        this.orderService = orderService;
        this.agents = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(4);
    }
    
    @PostConstruct
    public void initialize() {
        System.out.println("=".repeat(80));
        System.out.println("INITIALIZING TRADING SIMULATION");
        System.out.println("=".repeat(80));
        
        // Create trading agents
        createAgents();
        
        // Start simulation
        startSimulation();
        
        System.out.println("Simulation started with " + agents.size() + " trading agents");
        System.out.println("Agents will place orders every 2-5 seconds");
        System.out.println("=".repeat(80));
    }
    
    private void createAgents() {
        // AAPL Agents
        agents.add(new RetailTrader("Alice-Retail", "AAPL", 150.0, orderService));
        agents.add(new WhaleTrader("Hank-Whale", "AAPL", 150.0, orderService));
        agents.add(new RetailTrader("Eve-Momentum", "AAPL", 150.0, orderService));
        
        // BTC-USD Agents  
        agents.add(new RetailTrader("Grace-Retail", "BTC-USD", 45000.0, orderService));
        agents.add(new WhaleTrader("Bob-Whale", "BTC-USD", 45000.0, orderService));
        agents.add(new RetailTrader("Frank-HFT", "BTC-USD", 45000.0, orderService));
        
        System.out.println("Created trading agents:");
        for (TradingAgent agent : agents) {
            System.out.println("  - " + agent.getName() + " (" + agent.getSymbol() + ")");
        }
    }
    
    private void startSimulation() {
        // Schedule agent execution every 2-5 seconds
        scheduler.scheduleAtFixedRate(this::executeAgents, 2, 3, TimeUnit.SECONDS);
        
        // Schedule status updates every 10 seconds
        scheduler.scheduleAtFixedRate(this::printStatus, 10, 10, TimeUnit.SECONDS);
    }
    
    private void executeAgents() {
        for (TradingAgent agent : agents) {
            try {
                agent.executeStrategy();
            } catch (Exception e) {
                System.err.println("Error executing strategy for " + agent.getName() + ": " + e.getMessage());
            }
        }
    }
    
    private void printStatus() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("SIMULATION STATUS UPDATE");
        System.out.println("=".repeat(60));
        System.out.println("Active Agents: " + agents.stream().mapToInt(a -> a.isActive() ? 1 : 0).sum());
        
        try {
            // Get order counts
            var aaplOrders = orderService.getActiveOrders("AAPL");
            var btcOrders = orderService.getActiveOrders("BTC-USD");
            
            System.out.println("AAPL Active Orders: " + aaplOrders.size());
            System.out.println("BTC-USD Active Orders: " + btcOrders.size());
            
            if (!aaplOrders.isEmpty()) {
                System.out.println("Latest AAPL Order: " + aaplOrders.get(aaplOrders.size() - 1).getOrderId());
            }
            if (!btcOrders.isEmpty()) {
                System.out.println("Latest BTC-USD Order: " + btcOrders.get(btcOrders.size() - 1).getOrderId());
            }
            
        } catch (Exception e) {
            System.err.println("Error getting status: " + e.getMessage());
        }
        
        System.out.println("=".repeat(60) + "\n");
    }
    
    public void stopSimulation() {
        System.out.println("Stopping simulation...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Simulation stopped.");
    }

    public void restartSimulation() {
        stopSimulation();
        agents.clear();
        createAgents();
        this.scheduler = Executors.newScheduledThreadPool(4);
        startSimulation();
    }
    
    public List<TradingAgent> getAgents() {
        return new ArrayList<>(agents);
    }
}
