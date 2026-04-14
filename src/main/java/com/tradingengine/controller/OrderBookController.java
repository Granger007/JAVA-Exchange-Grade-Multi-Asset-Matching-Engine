package com.tradingengine.controller;

import com.tradingengine.service.OrderService;
import com.tradingengine.dto.OrderBookView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ORDER BOOK CONTROLLER - API endpoints for order book data
 * 
 * GOOD DESIGN: Clean MVC - Controller only handles HTTP and delegates to service
 * GRASP: Controller - Handles input/output, delegates business logic to services
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
    public ResponseEntity<OrderBookView> getOrderBook(@PathVariable String symbol) {
        OrderBookView orderBook = orderService.getOrderBookView(symbol);
        return ResponseEntity.ok(orderBook);
    }
}
