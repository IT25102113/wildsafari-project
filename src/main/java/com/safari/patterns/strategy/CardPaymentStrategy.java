package com.safari.patterns.strategy;

import com.safari.module.booking_mgmt.Booking;
import com.safari.module.finance_mgmt.Payment;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Component("cardPaymentStrategy")
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public Payment executePayment(Booking booking, PaymentDetails details) {
        String cardNumber = details.getCardNumber();
        if (cardNumber == null || cardNumber.replaceAll("\\s+", "").length() < 13) {
            throw new IllegalArgumentException("Invalid Credit/Debit card number format.");
        }

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaymentMethod("CARD_SANDBOX");
        payment.setPaymentStatus("PAID");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setPaymentReference("PAY-" + LocalDate.now().getYear() + "-" + ThreadLocalRandom.current().nextInt(10000, 99999));
        payment.setRemarks("Authorized via Sandbox Gateway (Card ending in " +
                cardNumber.substring(Math.max(0, cardNumber.length() - 4)) + ")");
        return payment;
    }
}
