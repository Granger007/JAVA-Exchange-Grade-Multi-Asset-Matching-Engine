package com.tradingengine.controller;

import com.tradingengine.dto.OrderRequest;
import com.tradingengine.dto.OrderResponse;
import com.tradingengine.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * TRADING CONTROLLER - Production REST endpoints for trading operations
 * 
 * GOOD DESIGN: Clean MVC architecture, proper dependency injection
 * SOLID: SRP - Only handles HTTP request/response processing
 * GRASP: Controller - Delegates business logic to service layer
 */
@RestController
@RequestMapping("/api/v1/trading")
@CrossOrigin(origins = "*")
public class TradingController {
    
    private final OrderService orderService;
    
    @Autowired
    public TradingController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * Place new order
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        try {
            OrderResponse response = orderService.placeOrder(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Cancel order
     */
    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        try {
            boolean cancelled = orderService.cancelOrder(orderId);
            return cancelled ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String orderId) {
        try {
            Optional<OrderResponse> order = orderService.getOrder(orderId);
            return order.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get orders for trader
     */
    @GetMapping("/orders/trader/{traderId}")
    public ResponseEntity<List<OrderResponse>> getOrdersForTrader(@PathVariable String traderId) {
        try {
            List<OrderResponse> orders = orderService.getOrdersForTrader(traderId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get active orders for symbol
     */
    @GetMapping("/orders/active/{symbol}")
    public ResponseEntity<List<OrderResponse>> getActiveOrders(@PathVariable String symbol) {
        try {
            List<OrderResponse> orders = orderService.getActiveOrders(symbol);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
