package com.tradingengine.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * MATCHING STRATEGY FACTORY - Creates matching strategies based on configuration
 * 
 * GOOD DESIGN: Factory Pattern
 */
@Component
public class MatchingStrategyFactory {
    
    private final Map<String, MatchingStrategy> strategies;

    @Autowired
    public MatchingStrategyFactory(List<MatchingStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                    strategy -> strategy.getStrategyName().toUpperCase(),
                    Function.identity()
                ));
    }

    public MatchingStrategy getStrategy(String name) {
        if (name == null) {
            return strategies.get("FIFO"); // Default
        }
        return strategies.getOrDefault(name.toUpperCase(), strategies.get("FIFO"));
    }
}
