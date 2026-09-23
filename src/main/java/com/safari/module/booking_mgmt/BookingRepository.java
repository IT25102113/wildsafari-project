package com.safari.module.booking_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingReference(String bookingReference);
    List<Booking> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Booking> findByBookingStatus(String bookingStatus);
    List<Booking> findByTripDate(LocalDate tripDate);
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(b) FROM Booking b WHERE b.safariPackage.id = :packageId")
    long countByPackageId(Long packageId);
    List<Booking> findAllByOrderByCreatedAtDesc();
}
