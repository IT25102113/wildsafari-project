package com.safari.patterns.strategy;

import com.safari.module.booking_mgmt.Booking;
import com.safari.module.finance_mgmt.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component("bankTransferPaymentStrategy")
public class BankTransferPaymentStrategy implements PaymentStrategy {

    @Value("${safari.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public Payment executePayment(Booking booking, PaymentDetails details) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaymentMethod("BANK_TRANSFER");
        payment.setPaymentStatus("PENDING");
        payment.setTransactionDate(LocalDateTime.now());
        payment.setPaymentReference("PAY-BT-" + LocalDate.now().getYear() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999));
        payment.setRemarks("Bank Transfer Slip Reference: " + details.getBankRef());

        MultipartFile slipFile = details.getSlipFile();
        if (slipFile != null && !slipFile.isEmpty()) {
            try {
                Path root = Paths.get(uploadDir);
                if (!Files.exists(root)) Files.createDirectories(root);
                String fileName = "slip_" + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                Files.copy(slipFile.getInputStream(), root.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                payment.setBankSlipImage("/uploads/" + fileName);
            } catch (IOException e) {
                System.err.println("Failed to save bank slip: " + e.getMessage());
            }
        }
        return payment;
    }
}
