package com.safari.module.allocation_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripAllocationRepository extends JpaRepository<TripAllocation, Long> {
    Optional<TripAllocation> findByBookingId(Long bookingId);
    List<TripAllocation> findByAllocationDate(LocalDate date);

    @Query("SELECT COUNT(a) > 0 FROM TripAllocation a WHERE a.guide.id = :guideId AND a.allocationDate = :date AND a.status IN ('ASSIGNED', 'IN_PROGRESS') AND (:excludeId IS NULL OR a.id != :excludeId)")
    boolean isGuideBusyOnDate(Long guideId, LocalDate date, Long excludeId);

    @Query("SELECT COUNT(a) > 0 FROM TripAllocation a WHERE a.vehicle.id = :vehicleId AND a.allocationDate = :date AND a.status IN ('ASSIGNED', 'IN_PROGRESS') AND (:excludeId IS NULL OR a.id != :excludeId)")
    boolean isVehicleBusyOnDate(Long vehicleId, LocalDate date, Long excludeId);

    List<TripAllocation> findAllByOrderByAllocationDateDesc();
}
