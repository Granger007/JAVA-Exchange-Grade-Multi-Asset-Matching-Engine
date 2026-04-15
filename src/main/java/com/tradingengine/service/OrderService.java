package com.tradingengine.service;

import com.tradingengine.domain.model.*;
import com.tradingengine.dto.OrderRequest;
import com.tradingengine.dto.OrderResponse;
import com.tradingengine.dto.OrderBookView;
import com.tradingengine.repository.OrderRepository;
import com.tradingengine.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import com.tradingengine.strategy.MatchingStrategy;
import com.tradingengine.strategy.MatchingStrategyFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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
    private final TradeRepository tradeRepository;
    private final MatchingStrategyFactory matchingStrategyFactory;
    private final SimpMessagingTemplate messagingTemplate;
    private final PortfolioService portfolioService;
    
    private final Map<String, OrderBook> orderBooks = new ConcurrentHashMap<>();
    private final Map<String, String> symbolStrategies = new ConcurrentHashMap<>();
    
    @Autowired
    public OrderService(OrderRepository orderRepository, TradeRepository tradeRepository, 
                        MatchingStrategyFactory matchingStrategyFactory,
                        SimpMessagingTemplate messagingTemplate,
                        PortfolioService portfolioService) {
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
        this.matchingStrategyFactory = matchingStrategyFactory;
        this.messagingTemplate = messagingTemplate;
        this.portfolioService = portfolioService;
    }
    
    private OrderBook getOrderBook(String symbol) {
        return orderBooks.computeIfAbsent(symbol, OrderBook::new);
    }
    
    private final Map<String, List<Order>> stopOrders = new ConcurrentHashMap<>();

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
        orderRepository.save(order); // Save initial state
        
        if (order.getType() == OrderType.STOP_LOSS) {
            stopOrders.computeIfAbsent(order.getSymbol(), k -> new CopyOnWriteArrayList<>()).add(order);
            return createOrderResponse(order, List.of());
        }
        
        List<Trade> trades = executeMatching(order);
        return createOrderResponse(order, trades);
    }
    
    public void setStrategyForSymbol(String symbol, String strategyName) {
        symbolStrategies.put(symbol, strategyName.toUpperCase());
    }
    
    public String getStrategyForSymbol(String symbol) {
        return symbolStrategies.getOrDefault(symbol, "FIFO");
    }
    
    public Map<String, String> getAllStrategies() {
        Map<String, String> result = new HashMap<>();
        result.put("BTC-USD", getStrategyForSymbol("BTC-USD"));
        result.put("AAPL", getStrategyForSymbol("AAPL"));
        return result;
    }
    
    private List<Trade> executeMatching(Order order) {
        OrderBook orderBook = getOrderBook(order.getSymbol());
        String strategyName = getStrategyForSymbol(order.getSymbol());
        MatchingStrategy matchingStrategy = matchingStrategyFactory.getStrategy(strategyName);
        List<Trade> trades = matchingStrategy.match(order, orderBook);
        
        if (order.getType() == OrderType.MARKET) {
            if (order.getQuantity() > 0 && order.isActive()) {
                order.cancel(); 
            }
        } else if (order.isActive() && order.getQuantity() > 0) {
            orderBook.addOrder(order);
        }
        
        // Save trades and update resting orders
        for (Trade trade : trades) {
            tradeRepository.save(trade);
            String restingOrderId = trade.getBuyOrderId().equals(order.getOrderId()) ? trade.getSellOrderId() : trade.getBuyOrderId();
            orderRepository.findById(restingOrderId).ifPresent(orderRepository::save);
            
            portfolioService.processTrade(trade);
            messagingTemplate.convertAndSend("/topic/trades", trade);
            
            checkStopOrders(trade);
        }
        
        // Save final state of incoming order
        orderRepository.save(order);
        
        // Publish OrderBook update
        publishOrderBookUpdate(orderBook);
        
        return trades;
    }
    
    private void checkStopOrders(Trade trade) {
        List<Order> list = stopOrders.get(trade.getSymbol());
        if (list == null || list.isEmpty()) return;
        
        List<Order> triggered = new ArrayList<>();
        for (Order so : list) {
            if (!so.isActive()) continue;
            
            if (so.getSide() == OrderSide.SELL && trade.getPrice() <= so.getStopPrice()) {
                triggered.add(so);
            } else if (so.getSide() == OrderSide.BUY && trade.getPrice() >= so.getStopPrice()) {
                triggered.add(so);
            }
        }
        
        for (Order so : triggered) {
            list.remove(so);
            so.convertToMarket();
            executeMatching(so);
        }
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
        
        // Remove from order book
        OrderBook orderBook = getOrderBook(order.getSymbol());
        orderBook.removeOrder(orderId);
        
        order.cancel();
        orderRepository.save(order); // Update status
        
        publishOrderBookUpdate(orderBook);
        
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

    public void clearAllOrders() {
        orderRepository.deleteAll();
        orderBooks.clear();
        stopOrders.clear();
    }
    
    /**
     * Get order book view for API representation
     * 
     * @param symbol Trading symbol
     * @return Order book view with aggregated price levels
     */
    public OrderBookView getOrderBookView(String symbol) {
        List<OrderResponse> activeOrders = getActiveOrders(symbol);
        
        List<OrderBookView.PriceLevelView> bids = aggregateOrdersBySide(activeOrders, "BUY");
        List<OrderBookView.PriceLevelView> asks = aggregateOrdersBySide(activeOrders, "SELL");
        
        return new OrderBookView(symbol, bids, asks);
    }
    
    /**
     * Aggregate orders by side into price levels
     * 
     * @param orders List of orders to aggregate
     * @param side Order side (BUY/SELL)
     * @return List of price level views
     */
    private List<OrderBookView.PriceLevelView> aggregateOrdersBySide(List<OrderResponse> orders, String side) {
        Map<Double, PriceLevelData> priceLevels = new HashMap<>();
        
        for (OrderResponse order : orders) {
            if (order.getSide().equals(side)) {
                priceLevels.computeIfAbsent(order.getPrice(), k -> new PriceLevelData())
                          .addOrder(order);
            }
        }
        
        return priceLevels.entrySet().stream()
                .map(entry -> new OrderBookView.PriceLevelView(
                        entry.getKey(),
                        entry.getValue().totalQuantity,
                        entry.getValue().orderCount))
                .toList();
    }
    
    /**
     * Helper class for price level aggregation
     */
    private static class PriceLevelData {
        long totalQuantity = 0;
        int orderCount = 0;
        
        void addOrder(OrderResponse order) {
            totalQuantity += order.getQuantity();
            orderCount++;
        }
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
    
    private void publishOrderBookUpdate(OrderBook orderBook) {
        // Simplified order book representation for WebSocket clients
        Map<String, Object> update = new HashMap<>();
        update.put("symbol", orderBook.getSymbol());
        update.put("bestBid", orderBook.getBestBid());
        update.put("bestAsk", orderBook.getBestAsk());
        update.put("midPrice", orderBook.getMidPrice());
        update.put("spread", orderBook.getSpread());
        
        messagingTemplate.convertAndSend("/topic/orderbook/" + orderBook.getSymbol(), (Object) update);
        messagingTemplate.convertAndSend("/topic/orderbook", (Object) update);
    }
    
    private Order createOrderFromRequest(OrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        
        OrderType type = OrderType.LIMIT;
        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            type = OrderType.valueOf(request.getType().toUpperCase());
        }
        
        return new Order(
            orderId,
            request.getSymbol(),
            request.getPrice(),
            request.getStopPrice(),
            request.getQuantity(),
            OrderSide.valueOf(request.getSide().toUpperCase()),
            type,
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
            order.getStopPrice(),
            order.getOriginalQuantity(), // Use original quantity to show what was requested
            order.getSide().toString(),
            order.getType().toString(),
            order.getStatus().toString(),
            order.getTraderId(),
            order.getCreatedAt(),
            order.getLastModified(),
            trades
        );
    }
}
