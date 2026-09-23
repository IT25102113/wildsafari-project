package com.safari.patterns.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PeakSeasonPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculateTotalPrice(BigDecimal basePrice, int participants, BigDecimal seasonalMultiplier) {
        if (basePrice == null || participants <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal multiplier = (seasonalMultiplier != null && seasonalMultiplier.compareTo(BigDecimal.ZERO) > 0)
                ? seasonalMultiplier
                : new BigDecimal("1.25");
        return basePrice.multiply(BigDecimal.valueOf(participants))
                .multiply(multiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String getStrategyName() {
        return "Peak Season Dynamic Rate (+25%)";
    }
}
