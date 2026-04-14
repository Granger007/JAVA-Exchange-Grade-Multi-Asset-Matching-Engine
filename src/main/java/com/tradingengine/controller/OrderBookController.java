package com.tradingengine.controller;

import com.tradingengine.service.OrderService;
import com.tradingengine.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ORDER BOOK CONTROLLER - API endpoints for order book data
 * 
 * GOOD DESIGN: Provides order book data for frontend dashboard
 * Aggregates order data into price levels for visualization
 */
@RestController
@RequestMapping("/api/orderbook")
@CrossOrigin(origins = "*")
public class OrderBookController {
    
    private final OrderService orderService;
    
    @Autowired
    public OrderBookController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Get order book snapshot for a symbol
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<Map<String, Object>> getOrderBook(@PathVariable String symbol) {
        List<OrderResponse> activeOrders = orderService.getActiveOrders(symbol);
        
        Map<String, Object> orderBook = new HashMap<>();
        orderBook.put("symbol", symbol);
        orderBook.put("bids", aggregateOrders(activeOrders, "BUY"));
        orderBook.put("asks", aggregateOrders(activeOrders, "SELL"));
        
        return ResponseEntity.ok(orderBook);
    }
    
    /**
     * Aggregate orders by price level
     */
    private List<Map<String, Object>> aggregateOrders(List<OrderResponse> orders, String side) {
        Map<Double, PriceLevel> priceLevels = new HashMap<>();
        
        for (OrderResponse order : orders) {
            if (order.getSide().equals(side)) {
                priceLevels.computeIfAbsent(order.getPrice(), k -> new PriceLevel()).addOrder(order);
            }
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Double, PriceLevel> entry : priceLevels.entrySet()) {
            Map<String, Object> level = new HashMap<>();
            level.put("price", entry.getKey());
            level.put("totalQuantity", entry.getValue().totalQuantity);
            level.put("orderCount", entry.getValue().orderCount);
            result.add(level);
        }
        
        return result;
    }
    
    /**
     * Helper class for aggregating price levels
     */
    private static class PriceLevel {
        long totalQuantity = 0;
        int orderCount = 0;
        
        void addOrder(OrderResponse order) {
            totalQuantity += order.getQuantity();
            orderCount++;
        }
    }
}
