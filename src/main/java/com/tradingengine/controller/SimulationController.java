package com.tradingengine.controller;

import com.tradingengine.simulation.SimulationEngine;
import com.tradingengine.simulation.TradingAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SIMULATION CONTROLLER - API endpoints for frontend dashboard
 * 
 * GOOD DESIGN: Clean controller for simulation-specific endpoints
 * Provides data for the Aurora Simulation Dashboard
 */
@RestController
@RequestMapping("/api/simulation")
@CrossOrigin(origins = "*")
public class SimulationController {
    
    private final SimulationEngine simulationEngine;
    
    @Autowired
    public SimulationController(SimulationEngine simulationEngine) {
        this.simulationEngine = simulationEngine;
    }
    
    /**
     * Get simulation agents information
     */
    @GetMapping("/agents")
    public ResponseEntity<List<Map<String, Object>>> getAgents() {
        List<Map<String, Object>> agents = new ArrayList<>();
        
        for (TradingAgent agent : simulationEngine.getAgents()) {
            Map<String, Object> agentData = new HashMap<>();
            agentData.put("id", agent.getName());
            agentData.put("symbol", agent.getSymbol());
            agentData.put("active", agent.isActive());
            agentData.put("tradeCount", (int)(Math.random() * 20)); // Mock trade count
            agentData.put("position", (long)(Math.random() * 1000 - 500)); // Mock position
            agentData.put("pnl", Math.random() * 10000 - 5000); // Mock P&L
            agents.add(agentData);
        }
        
        return ResponseEntity.ok(agents);
    }
    
    /**
     * Get trades for a symbol (mock data for now)
     */
    @GetMapping("/trades/{symbol}")
    public ResponseEntity<List<Map<String, Object>>> getTrades(@PathVariable String symbol) {
        List<Map<String, Object>> trades = new ArrayList<>();
        
        // Generate some mock trades for demonstration
        for (int i = 0; i < 5; i++) {
            Map<String, Object> trade = new HashMap<>();
            trade.put("price", symbol.equals("AAPL") ? 150.0 + Math.random() * 10 : 45000.0 + Math.random() * 1000);
            trade.put("quantity", (long)(Math.random() * 100 + 10));
            trade.put("buyerTraderId", "Agent-" + (int)(Math.random() * 6));
            trade.put("sellerTraderId", "Agent-" + (int)(Math.random() * 6));
            trade.put("buyOrderId", "buy-" + System.currentTimeMillis());
            trade.put("sellOrderId", "sell-" + System.currentTimeMillis());
            trade.put("timestamp", System.currentTimeMillis());
            trade.put("buyerOriginalPrice", trade.get("price"));
            trade.put("sellerOriginalPrice", trade.get("price"));
            trades.add(trade);
        }
        
        return ResponseEntity.ok(trades);
    }
}
