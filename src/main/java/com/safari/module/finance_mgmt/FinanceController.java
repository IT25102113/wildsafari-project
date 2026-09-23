package com.safari.module.finance_mgmt;

import com.safari.common.UserSession;
import com.safari.module.booking_mgmt.Booking;
import com.safari.module.booking_mgmt.BookingService;
import com.safari.module.user_mgmt.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService financeService;
    private final BookingService bookingService;

    public FinanceController(FinanceService financeService, BookingService bookingService) {
        this.financeService = financeService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/finance/dashboard";
        }
        if (!"FINANCE_OFFICER".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("payments", financeService.getAllPayments());
        model.addAttribute("invoices", financeService.getAllInvoices());
        model.addAttribute("revenueData", financeService.getRevenueReport());
        model.addAttribute("unpaidBookings", bookingService.getAllBookings().stream()
                .filter(b -> !"PAID".equalsIgnoreCase(b.getPaymentStatus()) && !"CANCELLED".equalsIgnoreCase(b.getBookingStatus()))
                .toList());

        return "finance/dashboard";
    }

    @GetMapping("/checkout/{bookingId}")
    public String showCheckout(@PathVariable("bookingId") Long bookingId, Model model, HttpSession session) {
        Booking booking = bookingService.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        model.addAttribute("booking", booking);
        model.addAttribute("currentUser", UserSession.getLoggedInUser(session));
        model.addAttribute("currentRole", UserSession.getCurrentRole(session));
        return "finance/checkout";
    }

    @PostMapping("/pay/card/{bookingId}")
    public String processCardPayment(@PathVariable("bookingId") Long bookingId,
                                     @RequestParam("cardHolder") String cardHolder,
                                     @RequestParam("cardNumber") String cardNumber,
                                     @RequestParam("expiryDate") String expiryDate,
                                     @RequestParam("cvv") String cvv,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "customer@safari.lk";

        try {
            Payment payment = financeService.processCardPayment(bookingId, cardNumber, expiryDate, cvv, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Payment of LKR " + payment.getAmount() +
                    " authorized! Reference: " + payment.getPaymentReference());
            return "redirect:/bookings/confirmation/" + payment.getBooking().getBookingReference();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/finance/checkout/" + bookingId;
        }
    }

    @PostMapping("/pay/bank/{bookingId}")
    public String submitBankTransfer(@PathVariable("bookingId") Long bookingId,
                                     @RequestParam("bankRef") String bankRef,
                                     @RequestParam(value = "slipFile", required = false) MultipartFile slipFile,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "customer@safari.lk";

        try {
            Payment payment = financeService.submitBankTransfer(bookingId, slipFile, bankRef, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Bank transfer slip uploaded. Awaiting Finance verification.");
            return "redirect:/bookings/confirmation/" + payment.getBooking().getBookingReference();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/finance/checkout/" + bookingId;
        }
    }

    @PostMapping("/verify/{paymentId}")
    public String verifyPayment(@PathVariable("paymentId") Long paymentId,
                                @RequestParam("approve") boolean approve,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "finance@safari.lk";

        try {
            financeService.verifyBankPayment(paymentId, approve, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", approve ? "Payment verified and invoice generated." : "Payment rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/finance/dashboard";
    }

    @GetMapping("/invoice/{id}")
    public String viewInvoice(@PathVariable("id") Long id, Model model, HttpSession session) {
        Invoice invoice = financeService.findInvoiceById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        model.addAttribute("invoice", invoice);
        model.addAttribute("currentUser", UserSession.getLoggedInUser(session));
        model.addAttribute("currentRole", UserSession.getCurrentRole(session));
        return "finance/invoice";
    }

    @PostMapping("/invoice/void/{id}")
    public String voidInvoice(@PathVariable("id") Long id,
                              @RequestParam("reason") String reason,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "finance@safari.lk";

        try {
            financeService.voidInvoice(id, reason, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Invoice marked as VOID.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/finance/dashboard";
    }

    @PostMapping("/refund/{paymentId}")
    public String processRefund(@PathVariable("paymentId") Long paymentId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "finance@safari.lk";

        try {
            financeService.processRefund(paymentId, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Refund processed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/finance/dashboard";
    }
}
