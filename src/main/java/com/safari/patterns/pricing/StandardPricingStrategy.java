package com.safari.patterns.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class StandardPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculateTotalPrice(BigDecimal basePrice, int participants, BigDecimal seasonalMultiplier) {
        if (basePrice == null || participants <= 0) {
            return BigDecimal.ZERO;
        }
        return basePrice.multiply(BigDecimal.valueOf(participants)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "Standard Regular Pricing";
    }
}
