package com.tradingengine.strategy;

import com.tradingengine.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FIFOMatchingStrategyTest {

    private FIFOMatchingStrategy strategy;
    private OrderBook orderBook;

    @BeforeEach
    void setUp() {
        strategy = new FIFOMatchingStrategy();
        orderBook = new OrderBook("AAPL");
    }

    @Test
    void testNoMatch() {
        Order buyOrder = new Order("buy1", "AAPL", 150.0, 0.0, 100, OrderSide.BUY, OrderType.LIMIT, "trader1");
        Order sellOrder = new Order("sell1", "AAPL", 155.0, 0.0, 100, OrderSide.SELL, OrderType.LIMIT, "trader2");

        orderBook.addOrder(sellOrder);

        List<Trade> trades = strategy.match(buyOrder, orderBook);

        assertTrue(trades.isEmpty(), "Should not match when buy price is lower than sell price");
        assertEquals(100, buyOrder.getQuantity());
        assertEquals(100, sellOrder.getQuantity());
    }

    @Test
    void testFullMatch() {
        Order buyOrder = new Order("buy1", "AAPL", 150.0, 0.0, 100, OrderSide.BUY, OrderType.LIMIT, "trader1");
        Order sellOrder = new Order("sell1", "AAPL", 145.0, 0.0, 100, OrderSide.SELL, OrderType.LIMIT, "trader2");

        orderBook.addOrder(sellOrder);

        List<Trade> trades = strategy.match(buyOrder, orderBook);

        assertEquals(1, trades.size(), "Should have one trade");
        Trade trade = trades.get(0);
        
        assertEquals(100, trade.getQuantity());
        assertEquals(145.0, trade.getPrice()); // Execution price is resting order's price
        
        assertEquals(0, buyOrder.getQuantity(), "Buy order should be fully filled");
        assertEquals(0, sellOrder.getQuantity(), "Sell order should be fully filled");
        
        assertTrue(buyOrder.isFilled());
        assertTrue(sellOrder.isFilled());
    }

    @Test
    void testPartialMatch() {
        Order buyOrder = new Order("buy1", "AAPL", 150.0, 0.0, 100, OrderSide.BUY, OrderType.LIMIT, "trader1");
        Order sellOrder = new Order("sell1", "AAPL", 150.0, 0.0, 40, OrderSide.SELL, OrderType.LIMIT, "trader2");

        orderBook.addOrder(sellOrder);

        List<Trade> trades = strategy.match(buyOrder, orderBook);

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(40, trade.getQuantity());
        assertEquals(150.0, trade.getPrice());

        assertEquals(60, buyOrder.getQuantity(), "Buy order should have 60 units remaining");
        assertEquals(0, sellOrder.getQuantity(), "Sell order should be fully filled");
        
        assertTrue(buyOrder.isActive());
        assertTrue(sellOrder.isFilled());
    }
}
