package com.tradingengine.strategy;

import com.tradingengine.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PRO RATA MATCHING STRATEGY
 * 
 * Matches orders proportionally based on their size at each price level.
 */
@Component
public class ProRataMatchingStrategy implements MatchingStrategy {

    @Override
    public List<Trade> match(Order incomingOrder, OrderBook orderBook) {
        List<Trade> trades = new ArrayList<>();
        
        if (!incomingOrder.isActive()) {
            return trades;
        }

        List<PriceLevel> oppositeLevels = getOppositePriceLevels(incomingOrder, orderBook);
        
        for (PriceLevel level : oppositeLevels) {
            if (!canMatchAtPrice(incomingOrder, level.getPrice())) {
                break;
            }
            
            List<Order> ordersAtLevel = new ArrayList<>(level.getOrders());
            long totalQuantityAtLevel = level.getTotalQuantity();
            
            if (totalQuantityAtLevel == 0) continue;
            
            // Calculate proportional match for each order
            long remainingIncomingQuantity = incomingOrder.getQuantity();
            double tradePrice = level.getPrice();
            
            int activeOrdersCount = 0;
            for (Order order : ordersAtLevel) {
                if (order.isActive()) activeOrdersCount++;
            }
            
            if (activeOrdersCount == 0) continue;
            
            for (int i = 0; i < ordersAtLevel.size(); i++) {
                Order restingOrder = ordersAtLevel.get(i);
                
                if (!restingOrder.isActive() || remainingIncomingQuantity <= 0) {
                    continue;
                }
                
                // ProRata logic
                long tradeQuantity = 0;
                
                // If it's the last active order, match the remaining to prevent rounding drop-offs
                boolean isLastActive = (i == ordersAtLevel.size() - 1); // Simple approximation
                
                if (isLastActive) {
                    tradeQuantity = Math.min(remainingIncomingQuantity, restingOrder.getQuantity());
                } else {
                    double proportion = (double) restingOrder.getQuantity() / totalQuantityAtLevel;
                    tradeQuantity = (long) Math.floor(proportion * incomingOrder.getOriginalQuantity());
                    
                    // Don't execute more than what's available
                    tradeQuantity = Math.min(tradeQuantity, remainingIncomingQuantity);
                    tradeQuantity = Math.min(tradeQuantity, restingOrder.getQuantity());
                    
                    if (tradeQuantity == 0 && remainingIncomingQuantity > 0) {
                        tradeQuantity = 1; // Minimum 1 unit if proportional logic rounds to 0
                    }
                }
                
                if (tradeQuantity <= 0) continue;

                Trade trade = executeTrade(incomingOrder, restingOrder, tradePrice, tradeQuantity);
                trades.add(trade);
                
                incomingOrder.execute(tradeQuantity, tradePrice);
                restingOrder.execute(tradeQuantity, tradePrice);
                remainingIncomingQuantity -= tradeQuantity;
                
                if (restingOrder.isFilled()) {
                    orderBook.removeOrder(restingOrder.getOrderId());
                }
                
                if (incomingOrder.getQuantity() == 0) {
                    break;
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
            return true;
        }
        if (incomingOrder.getSide() == OrderSide.BUY) {
            return incomingOrder.getPrice() >= price;
        } else {
            return incomingOrder.getPrice() <= price;
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
        return "PRORATA";
    }
}
