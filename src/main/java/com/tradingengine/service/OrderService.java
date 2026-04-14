package com.tradingengine.service;

import com.tradingengine.domain.model.*;
import com.tradingengine.dto.OrderRequest;
import com.tradingengine.dto.OrderResponse;
import com.tradingengine.repository.OrderRepository;
import com.tradingengine.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * ORDER SERVICE - Business logic for order operations
 * 
 * GOOD DESIGN: Clean separation of concerns, proper transaction management
 * MVC: Service layer handling business logic, delegating to domain layer
 * GRASP: Controller - Coordinates order processing workflow
 * SOLID: SRP - Only handles order-related business operations
 */
@Service
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository; // TODO: Use for saving trades when matching is implemented (currently unused placeholder)
    
    @Autowired
    public OrderService(OrderRepository orderRepository, TradeRepository tradeRepository) {
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }
    
    /**
     * Place new order
     * 
     * @param request Order request
     * @return Order response with execution details
     */
    public OrderResponse placeOrder(OrderRequest request) {
        validateOrderRequest(request);
        
        // Create order
        Order order = createOrderFromRequest(request);
        
        // For now, return a simple response without matching
        // In a full implementation, this would delegate to MatchingEngine
        orderRepository.save(order);
        
        return createOrderResponse(order, List.of());
    }
    
    /**
     * Cancel existing order
     * 
     * @param orderId Order ID to cancel
     * @return true if cancelled successfully
     */
    public boolean cancelOrder(String orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            return false;
        }
        
        Order order = orderOpt.get();
        if (!order.isActive()) {
            return false; // Cannot cancel inactive orders
        }
        
        order.cancel();
        orderRepository.save(order); // Update status
        
        return true;
    }
    
    /**
     * Get order by ID
     * 
     * @param orderId Order ID
     * @return Order details
     */
    public Optional<OrderResponse> getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .map(this::createOrderResponse);
    }
    
    /**
     * Get orders for trader
     * 
     * @param traderId Trader ID
     * @return List of orders
     */
    public List<OrderResponse> getOrdersForTrader(String traderId) {
        return orderRepository.findByTraderId(traderId)
                .stream()
                .map(this::createOrderResponse)
                .toList();
    }
    
    /**
     * Get active orders for symbol
     * 
     * @param symbol Trading symbol
     * @return List of active orders
     */
    public List<OrderResponse> getActiveOrders(String symbol) {
        return orderRepository.findActiveOrdersBySymbol(symbol)
                .stream()
                .map(this::createOrderResponse)
                .toList();
    }
    
    // Private helper methods
    private void validateOrderRequest(OrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }
        if (request.getSymbol() == null || request.getSymbol().trim().isEmpty()) {
            throw new IllegalArgumentException("Symbol is required");
        }
        if (request.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (request.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (request.getSide() == null) {
            throw new IllegalArgumentException("Order side is required");
        }
        if (request.getTraderId() == null || request.getTraderId().trim().isEmpty()) {
            throw new IllegalArgumentException("Trader ID is required");
        }
    }
    
    private Order createOrderFromRequest(OrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        return new Order(
            orderId,
            request.getSymbol(),
            request.getPrice(),
            request.getQuantity(),
            OrderSide.valueOf(request.getSide().toUpperCase()),
            request.getTraderId()
        );
    }
    
    private OrderResponse createOrderResponse(Order order) {
        return createOrderResponse(order, List.of());
    }
    
    private OrderResponse createOrderResponse(Order order, List<Trade> trades) {
        return new OrderResponse(
            order.getOrderId(),
            order.getSymbol(),
            order.getPrice(),
            order.getQuantity(),
            order.getSide().toString(),
            order.getStatus().toString(),
            order.getTraderId(),
            order.getCreatedAt(),
            order.getLastModified(),
            trades
        );
    }
}
