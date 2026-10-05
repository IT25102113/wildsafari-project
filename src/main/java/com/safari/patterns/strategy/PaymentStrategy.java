package com.safari.patterns.strategy;

import com.safari.module.booking_mgmt.Booking;
import com.safari.module.finance_mgmt.Payment;

public interface PaymentStrategy {
    Payment executePayment(Booking booking, PaymentDetails details);
}
