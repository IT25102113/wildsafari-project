package com.safari.patterns.pricing;

import java.math.BigDecimal;

public class PricingContext {

    private PricingStrategy strategy;

    public PricingContext(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PricingStrategy strategy) {
        this.strategy = strategy;
    }

    public BigDecimal executePricing(BigDecimal basePrice, int participants, BigDecimal seasonalMultiplier) {
        if (strategy == null) {
            this.strategy = new StandardPricingStrategy();
        }
        return strategy.calculateTotalPrice(basePrice, participants, seasonalMultiplier);
    }
}
