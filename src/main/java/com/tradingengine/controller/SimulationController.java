package com.tradingengine.controller;

import com.tradingengine.domain.model.Portfolio;
import com.tradingengine.domain.model.Trade;
import com.tradingengine.repository.TradeRepository;
import com.tradingengine.service.OrderService;
import com.tradingengine.service.PortfolioService;
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
    private final TradeRepository tradeRepository;
    private final PortfolioService portfolioService;
    private final OrderService orderService;
    
    @Autowired
    public SimulationController(SimulationEngine simulationEngine, 
                                TradeRepository tradeRepository,
                                PortfolioService portfolioService,
                                OrderService orderService) {
        this.simulationEngine = simulationEngine;
        this.tradeRepository = tradeRepository;
        this.portfolioService = portfolioService;
        this.orderService = orderService;
    }
    
    /**
     * Get simulation agents information
     */
    @GetMapping("/agents")
    public ResponseEntity<List<Map<String, Object>>> getAgents() {
        List<Map<String, Object>> agents = new ArrayList<>();
        
        for (TradingAgent agent : simulationEngine.getAgents()) {
            Portfolio portfolio = portfolioService.getPortfolio(agent.getAgentId());
            Map<String, Object> agentData = new HashMap<>();
            agentData.put("id", agent.getName());
            agentData.put("symbol", agent.getSymbol());
            agentData.put("active", agent.isActive());
            
            List<Trade> agentTrades = tradeRepository.findByTraderId(agent.getAgentId());
            agentData.put("tradeCount", agentTrades.size());
            agentData.put("position", portfolio.getPositions().getOrDefault(agent.getSymbol(), 0L));
            agentData.put("pnl", portfolio.getBalance() - 100000.0); // Assuming 100k starting balance
            agents.add(agentData);
        }
        
        return ResponseEntity.ok(agents);
    }
    
    /**
     * Get trades for a symbol
     */
    @GetMapping("/trades/{symbol}")
    public ResponseEntity<List<Map<String, Object>>> getTrades(@PathVariable String symbol) {
        List<Trade> trades = tradeRepository.findRecentTrades(symbol, 50);
        List<Map<String, Object>> tradeList = new ArrayList<>();
        
        for (Trade trade : trades) {
            Map<String, Object> tradeData = new HashMap<>();
            tradeData.put("price", trade.getPrice());
            tradeData.put("quantity", trade.getQuantity());
            
            // Resolve agent names for better UI display
            String buyerName = simulationEngine.getAgents().stream()
                .filter(a -> a.getAgentId().equals(trade.getBuyTraderId()))
                .map(TradingAgent::getName)
                .findFirst()
                .orElse(trade.getBuyTraderId().length() > 8 ? trade.getBuyTraderId().substring(0, 8) : trade.getBuyTraderId());
                
            String sellerName = simulationEngine.getAgents().stream()
                .filter(a -> a.getAgentId().equals(trade.getSellTraderId()))
                .map(TradingAgent::getName)
                .findFirst()
                .orElse(trade.getSellTraderId().length() > 8 ? trade.getSellTraderId().substring(0, 8) : trade.getSellTraderId());

            tradeData.put("buyerTraderId", buyerName);
            tradeData.put("sellerTraderId", sellerName);
            tradeData.put("buyOrderId", trade.getBuyOrderId());
            tradeData.put("sellOrderId", trade.getSellOrderId());
            tradeData.put("timestamp", trade.getExecutedAt().toEpochMilli());
            
            // These would normally come from the original orders, for demo we'll use trade price
            tradeData.put("buyerOriginalPrice", trade.getPrice());
            tradeData.put("sellerOriginalPrice", trade.getPrice());
            tradeList.add(tradeData);
        }
        
        return ResponseEntity.ok(tradeList);
    }

    /**
     * Restart the simulation
     */
    @PostMapping("/restart")
    public ResponseEntity<Void> restartSimulation() {
        orderService.clearAllOrders();
        tradeRepository.deleteAll();
        portfolioService.clearAllPortfolios();
        simulationEngine.restartSimulation();
        return ResponseEntity.ok().build();
    }
}
