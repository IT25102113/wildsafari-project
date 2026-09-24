package com.safari.module.finance_mgmt;

import com.safari.common.ActivityLogService;
import com.safari.module.booking_mgmt.Booking;
import com.safari.module.booking_mgmt.BookingRepository;
import com.safari.patterns.factory.ReferenceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FinanceService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final ActivityLogService activityLogService;

    @Value("${safari.upload.dir:uploads}")
    private String uploadDir;

    public FinanceService(PaymentRepository paymentRepository,
                          InvoiceRepository invoiceRepository,
                          BookingRepository bookingRepository,
                          ActivityLogService activityLogService) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.bookingRepository = bookingRepository;
        this.activityLogService = activityLogService;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByTransactionDateDesc();
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAllByOrderByInvoiceDateDesc();
    }

    public Optional<Invoice> findInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> findInvoiceByBookingId(Long bookingId) {
        return invoiceRepository.findByBookingId(bookingId);
    }

    @Transactional
    public Payment processCardPayment(Long bookingId, String cardNumber, String expiryDate, String cvv, String actorEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Validate mock card
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

        Payment savedPayment = paymentRepository.save(payment);

        // Update booking state
        booking.setPaymentStatus("PAID");
        if ("PENDING".equalsIgnoreCase(booking.getBookingStatus())) {
            booking.setBookingStatus("CONFIRMED");
        }
        bookingRepository.save(booking);

        // Auto-generate formal invoice via Factory Pattern
        generateInvoice(savedPayment, booking, actorEmail);

        activityLogService.publishActivity(
                actorEmail,
                "FINANCE_OFFICER",
                "Payment & Invoice",
                "PAYMENT_SUCCESS",
                "Payment " + savedPayment.getPaymentReference() + " completed for booking " + booking.getBookingReference() +
                        " (LKR " + savedPayment.getAmount() + ")"
        );

        return savedPayment;
    }

    @Transactional
    public Payment submitBankTransfer(Long bookingId, MultipartFile slipFile, String bankRef, String actorEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalPrice());
        payment.setPaymentMethod("BANK_TRANSFER");
        payment.setPaymentStatus("PENDING"); // Pending finance approval
        payment.setTransactionDate(LocalDateTime.now());
        payment.setPaymentReference("PAY-BT-" + LocalDate.now().getYear() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999));
        payment.setRemarks("Bank Transfer Slip Reference: " + bankRef);

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

        Payment saved = paymentRepository.save(payment);

        activityLogService.publishActivity(
                actorEmail,
                "FINANCE_OFFICER",
                "Payment & Invoice",
                "BANK_TRANSFER_SUBMITTED",
                "Bank transfer slip uploaded for " + booking.getBookingReference()
        );

        return saved;
    }

    @Transactional
    public void verifyBankPayment(Long paymentId, boolean approve, String actorEmail) {
        paymentRepository.findById(paymentId).ifPresent(payment -> {
            Booking booking = payment.getBooking();
            if (approve) {
                payment.setPaymentStatus("PAID");
                booking.setPaymentStatus("PAID");
                if ("PENDING".equalsIgnoreCase(booking.getBookingStatus())) {
                    booking.setBookingStatus("CONFIRMED");
                }
                generateInvoice(payment, booking, actorEmail);
            } else {
                payment.setPaymentStatus("FAILED");
                payment.setRemarks(payment.getRemarks() + " [Rejected by Finance]");
            }
            paymentRepository.save(payment);
            bookingRepository.save(booking);

            activityLogService.publishActivity(
                    actorEmail,
                    "FINANCE_OFFICER",
                    "Payment & Invoice",
                    approve ? "PAYMENT_APPROVED" : "PAYMENT_REJECTED",
                    "Payment " + payment.getPaymentReference() + (approve ? " verified and approved." : " rejected.")
            );
        });
    }

    @Transactional
    public Invoice generateInvoice(Payment payment, Booking booking, String actorEmail) {
        BigDecimal total = payment.getAmount();
        // 10% VAT tax extraction: subtotal = total / 1.10, tax = total - subtotal
        BigDecimal subtotal = total.divide(new BigDecimal("1.10"), 2, RoundingMode.HALF_UP);
        BigDecimal tax = total.subtract(subtotal);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(ReferenceFactory.generateInvoiceNumber());
        invoice.setPayment(payment);
        invoice.setBooking(booking);
        invoice.setSubtotal(subtotal);
        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);
        invoice.setInvoiceDate(LocalDate.now());
        invoice.setStatus("ISSUED");
        invoice.setNotes("Includes DWC park conservation fee and certified 4x4 naturalist safari services.");

        Invoice saved = invoiceRepository.save(invoice);

        activityLogService.publishActivity(
                actorEmail,
                "FINANCE_OFFICER",
                "Payment & Invoice",
                "INVOICE_GENERATED",
                "Generated Tax Invoice " + saved.getInvoiceNumber() + " for amount LKR " + saved.getTotalAmount()
        );

        return saved;
    }

    @Transactional
    public void voidInvoice(Long invoiceId, String reason, String actorEmail) {
        invoiceRepository.findById(invoiceId).ifPresent(inv -> {
            inv.setStatus("VOID");
            inv.setNotes(inv.getNotes() + " | VOID REASON: " + reason);
            invoiceRepository.save(inv);
            activityLogService.publishActivity(
                    actorEmail,
                    "FINANCE_OFFICER",
                    "Payment & Invoice",
                    "VOID_INVOICE",
                    "Invoice " + inv.getInvoiceNumber() + " marked as VOID."
            );
        });
    }

    @Transactional
    public void processRefund(Long paymentId, String actorEmail) {
        paymentRepository.findById(paymentId).ifPresent(payment -> {
            payment.setPaymentStatus("REFUNDED");
            Booking booking = payment.getBooking();
            booking.setPaymentStatus("REFUNDED");
            paymentRepository.save(payment);
            bookingRepository.save(booking);

            activityLogService.publishActivity(
                    actorEmail,
                    "FINANCE_OFFICER",
                    "Payment & Invoice",
                    "REFUND_PROCESSED",
                    "Processed refund for payment " + payment.getPaymentReference() + " (LKR " + payment.getAmount() + ")"
            );
        });
    }

    @Transactional
    public void deleteInvoice(Long invoiceId, String actorEmail) {
        invoiceRepository.findById(invoiceId).ifPresent(inv -> {
            String invNum = inv.getInvoiceNumber();
            invoiceRepository.delete(inv);
            activityLogService.publishActivity(
                    actorEmail,
                    "FINANCE_OFFICER",
                    "Payment & Invoice",
                    "DELETE_INVOICE",
                    "Tax Invoice " + invNum + " permanently deleted."
            );
        });
    }

    @Transactional
    public void deletePayment(Long paymentId, String actorEmail) {
        paymentRepository.findById(paymentId).ifPresent(pay -> {
            String payRef = pay.getPaymentReference();
            Booking booking = pay.getBooking();

            // First delete any invoices attached to this payment
            List<Invoice> relatedInvoices = invoiceRepository.findAll().stream()
                    .filter(i -> i.getPayment() != null && i.getPayment().getId().equals(paymentId))
                    .toList();
            invoiceRepository.deleteAll(relatedInvoices);

            // Revert booking payment status if it was paid
            if (booking != null && "PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
                booking.setPaymentStatus("UNPAID");
                bookingRepository.save(booking);
            }

            paymentRepository.delete(pay);

            activityLogService.publishActivity(
                    actorEmail,
                    "FINANCE_OFFICER",
                    "Payment & Invoice",
                    "DELETE_PAYMENT",
                    "Payment transaction " + payRef + " permanently deleted."
            );
        });
    }

    public Map<String, Object> getRevenueReport() {
        List<Payment> paidPayments = paymentRepository.findAll().stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.getPaymentStatus()))
                .toList();

        BigDecimal totalRevenue = paidPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> report = new HashMap<>();
        report.put("totalRevenue", totalRevenue);
        report.put("paidTransactionsCount", paidPayments.size());
        report.put("recentPayments", paymentRepository.findAllByOrderByTransactionDateDesc());
        return report;
    }
}
