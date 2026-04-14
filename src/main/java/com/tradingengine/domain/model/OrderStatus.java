package com.tradingengine.domain.model;

/**
 * ORDERSTATUS - Enum representing order lifecycle states
 * Type-safe enumeration with clear state transitions
 */
public enum OrderStatus {
    NEW,
    PARTIALLY_FILLED,
    FILLED,
    CANCELLED,
    REJECTED;
    
    public boolean isActive() {
        return this == NEW || this == PARTIALLY_FILLED;
    }
    
    public boolean isFinal() {
        return this == FILLED || this == CANCELLED || this == REJECTED;
    }
}
