package com.safari.patterns.pricing;

import java.math.BigDecimal;

/**
 * Strategy Pattern Interface for Safari Pricing Calculations.
 * Allows dynamic pricing computation based on seasons, group size tiers, and special conditions.
 */
public interface PricingStrategy {
    BigDecimal calculateTotalPrice(BigDecimal basePrice, int participants, BigDecimal seasonalMultiplier);
    String getStrategyName();
}
