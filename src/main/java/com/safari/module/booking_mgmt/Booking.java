package com.safari.module.booking_mgmt;

import com.safari.module.package_mgmt.SafariPackage;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "booking_reference", nullable = false, unique = true)
    private String bookingReference;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "package_id", nullable = false)
    private SafariPackage safariPackage;

    @Column(name = "customer_id")
    private Long customerId;

    @NotBlank(message = "Customer full name is required")
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email format")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Email format must be valid (e.g. yourname@gmail.com)")
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^0[0-9]{9}$|^[0-9]{10}$", message = "Phone number must be exactly 10 digits (e.g. 0771234567)")
    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @NotNull(message = "Trip date is required")
    @FutureOrPresent(message = "Trip date cannot be in the past")
    @Column(name = "trip_date", nullable = false)
    private LocalDate tripDate;

    @Min(value = 1, message = "At least 1 participant is required")
    @Column(name = "participant_count", nullable = false)
    private int participantCount = 1;

    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;

    @NotNull
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "booking_status", nullable = false)
    private String bookingStatus = "PENDING"; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus = "UNPAID"; // UNPAID, PAID, REFUNDED

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingParticipant> participants = new ArrayList<>();

    public Booking() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    public SafariPackage getSafariPackage() { return safariPackage; }
    public void setSafariPackage(SafariPackage safariPackage) { this.safariPackage = safariPackage; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public LocalDate getTripDate() { return tripDate; }
    public void setTripDate(LocalDate tripDate) { this.tripDate = tripDate; }
    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<BookingParticipant> getParticipants() { return participants; }
    public void setParticipants(List<BookingParticipant> participants) { this.participants = participants; }
}
