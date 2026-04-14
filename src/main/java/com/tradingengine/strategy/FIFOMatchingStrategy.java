package com.tradingengine.strategy;

import com.tradingengine.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * FIFO MATCHING STRATEGY - Price-Time Priority matching
 * 
 * GOOD DESIGN: Clear responsibility, follows price-time priority rules
 * SOLID: SRP - Only handles FIFO matching logic
 * LSP - Can be substituted with any other MatchingStrategy
 * OCP - New strategies can be added without modifying this
 */
@Component
public class FIFOMatchingStrategy implements MatchingStrategy {
    
    @Override
    public List<Trade> match(Order incomingOrder, OrderBook orderBook) {
        List<Trade> trades = new ArrayList<>();
        
        if (!incomingOrder.isActive()) {
            return trades;
        }

        List<PriceLevel> oppositeLevels = getOppositePriceLevels(incomingOrder, orderBook);
        
        for (PriceLevel level : oppositeLevels) {
            if (!canMatchAtPrice(incomingOrder, level.getPrice())) {
                break; // No more favorable prices
            }
            
            List<Order> ordersAtLevel = new ArrayList<>(level.getOrders());
            
            for (Order restingOrder : ordersAtLevel) {
                if (!restingOrder.isActive() || incomingOrder.getQuantity() == 0) {
                    continue;
                }
                
                long tradeQuantity = Math.min(incomingOrder.getQuantity(), restingOrder.getQuantity());
                double tradePrice = level.getPrice(); // Use resting order price
                
                // Execute trade
                Trade trade = executeTrade(incomingOrder, restingOrder, tradePrice, tradeQuantity);
                trades.add(trade);
                
                // Update order quantities and status
                incomingOrder.execute(tradeQuantity, tradePrice);
                restingOrder.execute(tradeQuantity, tradePrice);
                
                // Remove filled orders from book
                if (restingOrder.isFilled()) {
                    orderBook.removeOrder(restingOrder.getOrderId());
                }
                
                if (incomingOrder.getQuantity() == 0) {
                    break; // Incoming order fully filled
                }
            }
        }
        
        return trades;
    }
    
    private List<PriceLevel> getOppositePriceLevels(Order order, OrderBook orderBook) {
        if (order.getSide() == OrderSide.BUY) {
            return orderBook.getAskLevels(Integer.MAX_VALUE);
        } else {
            return orderBook.getBidLevels(Integer.MAX_VALUE);
        }
    }
    
    private boolean canMatchAtPrice(Order incomingOrder, double price) {
        if (incomingOrder.getType() == OrderType.MARKET) {
            return true; // Market orders match at any available price
        }
        
        if (incomingOrder.getSide() == OrderSide.BUY) {
            return incomingOrder.getPrice() >= price; // Buy order matches at or below limit
        } else {
            return incomingOrder.getPrice() <= price; // Sell order matches at or above limit
        }
    }
    
    private Trade executeTrade(Order incomingOrder, Order restingOrder, 
                              double price, long quantity) {
        String buyOrderId = incomingOrder.getSide() == OrderSide.BUY ? 
                           incomingOrder.getOrderId() : restingOrder.getOrderId();
        String sellOrderId = incomingOrder.getSide() == OrderSide.SELL ? 
                            incomingOrder.getOrderId() : restingOrder.getOrderId();
        String buyTraderId = incomingOrder.getSide() == OrderSide.BUY ? 
                           incomingOrder.getTraderId() : restingOrder.getTraderId();
        String sellTraderId = incomingOrder.getSide() == OrderSide.SELL ? 
                            incomingOrder.getTraderId() : restingOrder.getTraderId();
        
        return new Trade(
            UUID.randomUUID().toString(),
            buyOrderId,
            sellOrderId,
            buyTraderId,
            sellTraderId,
            incomingOrder.getSymbol(),
            price,
            quantity
        );
    }
    
    @Override
    public String getStrategyName() {
        return "FIFO";
    }
}
