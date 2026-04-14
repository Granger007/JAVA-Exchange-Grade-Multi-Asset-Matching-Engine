package com.tradingengine.domain.model;

/**
 * ORDERSIDE - Enum representing order side (buy/sell)
 * Immutable type-safe enumeration
 */
public enum OrderSide {
    BUY,
    SELL;
    
    public OrderSide opposite() {
        return this == BUY ? SELL : BUY;
    }
}
