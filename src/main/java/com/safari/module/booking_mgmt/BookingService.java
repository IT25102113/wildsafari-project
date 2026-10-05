package com.safari.module.booking_mgmt;

import com.safari.common.ActivityLogService;
import com.safari.module.finance_mgmt.PaymentRepository;
import com.safari.module.package_mgmt.SafariPackage;
import com.safari.module.package_mgmt.SafariPackageRepository;
import com.safari.patterns.factory.ReferenceFactory;
import com.safari.patterns.pricing.PeakSeasonPricingStrategy;
import com.safari.patterns.pricing.PricingContext;
import com.safari.patterns.pricing.StandardPricingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingParticipantRepository participantRepository;
    private final SafariPackageRepository packageRepository;
    private final ActivityLogService activityLogService;
    private final PaymentRepository paymentRepository;

    public BookingService(BookingRepository bookingRepository,
                          BookingParticipantRepository participantRepository,
                          SafariPackageRepository packageRepository,
                          ActivityLogService activityLogService,
                          PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.participantRepository = participantRepository;
        this.packageRepository = packageRepository;
        this.activityLogService = activityLogService;
        this.paymentRepository = paymentRepository;
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = bookingRepository.findAllByOrderByCreatedAtDesc();
        syncPendingPayments(list);
        return list;
    }

    public List<Booking> getCustomerBookings(String email) {
        List<Booking> list = bookingRepository.findByCustomerEmailOrderByCreatedAtDesc(email);
        syncPendingPayments(list);
        return list;
    }

    public List<Booking> getCustomerBookingsById(Long customerId) {
        List<Booking> list = bookingRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        syncPendingPayments(list);
        return list;
    }

    public Optional<Booking> findById(Long id) {
        Optional<Booking> opt = bookingRepository.findById(id);
        opt.ifPresent(this::syncPendingPayment);
        return opt;
    }

    public Optional<Booking> findByReference(String reference) {
        Optional<Booking> opt = bookingRepository.findByBookingReference(reference);
        opt.ifPresent(this::syncPendingPayment);
        return opt;
    }

    private void syncPendingPayments(List<Booking> list) {
        if (list != null) {
            for (Booking b : list) {
                syncPendingPayment(b);
            }
        }
    }

    private void syncPendingPayment(Booking b) {
        if (b != null && "UNPAID".equalsIgnoreCase(b.getPaymentStatus()) && b.getId() != null) {
            boolean hasPending = paymentRepository.findByBookingId(b.getId()).stream()
                    .anyMatch(p -> "PENDING".equalsIgnoreCase(p.getPaymentStatus()));
            if (hasPending) {
                b.setPaymentStatus("PENDING");
                bookingRepository.save(b);
            }
        }
    }

    @Transactional
    public Booking createBooking(Booking booking, Long packageId, String actorEmail) {
        SafariPackage pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Safari package not found"));

        if ("DISCONTINUED".equalsIgnoreCase(pkg.getStatus())) {
            throw new IllegalStateException("This safari package has been discontinued and cannot accept new reservations.");
        }

        if (booking.getTripDate() == null || booking.getTripDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expedition date cannot be in the past. Please select today or a future date.");
        }

        if (booking.getParticipantCount() < 1) {
            throw new IllegalArgumentException("Participant count must be at least 1 passenger.");
        }

        if (booking.getParticipantCount() > pkg.getMaxGroupSize()) {
            throw new IllegalArgumentException("Participant count (" + booking.getParticipantCount() +
                    ") exceeds package maximum limit (" + pkg.getMaxGroupSize() + ").");
        }

        booking.setSafariPackage(pkg);
        booking.setBookingReference(ReferenceFactory.generateBookingReference());

        // Strategy Pattern Pricing Calculation:
        // July, August, December, January are considered Sri Lankan peak safari seasons
        int month = booking.getTripDate().getMonthValue();
        boolean isPeakSeason = (month == 7 || month == 8 || month == 12 || month == 1);
        PricingContext pricingContext = new PricingContext(
                isPeakSeason ? new PeakSeasonPricingStrategy() : new StandardPricingStrategy()
        );

        BigDecimal calculatedTotal = pricingContext.executePricing(
                pkg.getBasePrice(),
                booking.getParticipantCount(),
                pkg.getPeakSeasonMultiplier()
        );
        booking.setTotalPrice(calculatedTotal);
        booking.setBookingStatus("PENDING");
        booking.setPaymentStatus("UNPAID");

        Booking saved = bookingRepository.save(booking);

        activityLogService.publishActivity(
                actorEmail,
                "CUSTOMER",
                "Booking & Reservation",
                "CREATE_BOOKING",
                "Reservation " + saved.getBookingReference() + " booked for " + pkg.getName() + " on " + saved.getTripDate()
        );

        return saved;
    }

    @Transactional
    public Booking updateBooking(Long bookingId, LocalDate newDate, int newParticipants, String specialRequests, String actorEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // LOCK: Cannot edit if a PAID or PENDING payment exists
        boolean hasPayment = paymentRepository.findByBookingId(bookingId).stream()
                .anyMatch(p -> "PAID".equalsIgnoreCase(p.getPaymentStatus()) || "PENDING".equalsIgnoreCase(p.getPaymentStatus()));
        if (hasPayment) {
            throw new IllegalStateException(
                "This booking cannot be modified — a payment has already been submitted. " +
                "Please contact the tour operator for amendments.");
        }

        // 48-Hour Cut-off Policy check (applies if booking is already paid)
        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            long daysUntilTrip = ChronoUnit.DAYS.between(LocalDate.now(), booking.getTripDate());
            if (daysUntilTrip < 2) {
                throw new IllegalStateException("Modifications are only permitted at least 48 hours prior to the scheduled safari date.");
            }
        }

        if (newDate == null || newDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Rescheduled expedition date cannot be in the past.");
        }

        if (newParticipants < 1) {
            throw new IllegalArgumentException("Participant count must be at least 1 passenger.");
        }

        if (newParticipants > booking.getSafariPackage().getMaxGroupSize()) {
            throw new IllegalArgumentException("Participant count exceeds max package limit (" + booking.getSafariPackage().getMaxGroupSize() + ").");
        }

        booking.setTripDate(newDate);
        booking.setParticipantCount(newParticipants);
        booking.setSpecialRequests(specialRequests);

        // Recalculate price using peak season strategy if applicable
        int month = newDate.getMonthValue();
        boolean isPeak = (month == 7 || month == 8 || month == 12 || month == 1);
        PricingContext ctx = new PricingContext(isPeak ? new PeakSeasonPricingStrategy() : new StandardPricingStrategy());
        BigDecimal updatedTotal = ctx.executePricing(
                booking.getSafariPackage().getBasePrice(),
                newParticipants,
                booking.getSafariPackage().getPeakSeasonMultiplier()
        );
        booking.setTotalPrice(updatedTotal);

        Booking saved = bookingRepository.save(booking);

        activityLogService.publishActivity(
                actorEmail,
                "CUSTOMER",
                "Booking & Reservation",
                "UPDATE_BOOKING",
                "Booking " + saved.getBookingReference() + " updated — " + newParticipants + " pax, date: " + newDate + ", new total: LKR " + updatedTotal
        );

        return saved;
    }

    @Transactional
    public void cancelBooking(Long bookingId, String actorEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Retain historical record rather than hard-deleting
        booking.setBookingStatus("CANCELLED");
        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            booking.setPaymentStatus("REFUND_PENDING");
        }
        bookingRepository.save(booking);

        activityLogService.publishActivity(
                actorEmail,
                "CUSTOMER",
                "Booking & Reservation",
                "CANCEL_BOOKING",
                "Reservation " + booking.getBookingReference() + " marked as CANCELLED."
        );
    }

    @Transactional
    public void updateStatus(Long bookingId, String bookingStatus, String paymentStatus, String actorEmail) {
        bookingRepository.findById(bookingId).ifPresent(b -> {
            b.setBookingStatus(bookingStatus);
            if (paymentStatus != null) {
                b.setPaymentStatus(paymentStatus);
            }
            bookingRepository.save(b);
            activityLogService.publishActivity(
                    actorEmail,
                    "OPERATIONS_MANAGER",
                    "Booking & Reservation",
                    "STATUS_CHANGE",
                    "Booking " + b.getBookingReference() + " status updated to " + bookingStatus
            );
        });
    }

    public long getBookingCountForPackage(Long packageId) {
        return bookingRepository.countByPackageId(packageId);
    }
}
