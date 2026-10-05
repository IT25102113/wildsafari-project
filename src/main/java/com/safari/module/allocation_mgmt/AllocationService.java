package com.safari.module.allocation_mgmt;

import com.safari.common.ActivityLogService;
import com.safari.common.NotificationService;
import com.safari.module.booking_mgmt.Booking;
import com.safari.module.booking_mgmt.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AllocationService {

    private final GuideRepository guideRepository;
    private final VehicleRepository vehicleRepository;
    private final TripAllocationRepository allocationRepository;
    private final BookingRepository bookingRepository;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;
    private final GuideAvailabilityRepository availabilityRepository;

    public AllocationService(GuideRepository guideRepository,
                             VehicleRepository vehicleRepository,
                             TripAllocationRepository allocationRepository,
                             BookingRepository bookingRepository,
                             ActivityLogService activityLogService,
                             NotificationService notificationService,
                             GuideAvailabilityRepository availabilityRepository) {
        this.guideRepository = guideRepository;
        this.vehicleRepository = vehicleRepository;
        this.allocationRepository = allocationRepository;
        this.bookingRepository = bookingRepository;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
        this.availabilityRepository = availabilityRepository;
    }

    // Guide operations
    public List<Guide> getAllGuides() { return guideRepository.findAll(); }
    public List<Guide> getActiveGuides() { return guideRepository.findByStatus("ACTIVE"); }
    public Optional<Guide> findGuideById(Long id) { return guideRepository.findById(id); }

    @Transactional
    public Guide saveGuide(Guide guide, String actorEmail) {
        boolean isNew = (guide.getId() == null);
        Guide saved = guideRepository.save(guide);
        activityLogService.publishActivity(
                actorEmail,
                "OPERATIONS_MANAGER",
                "Guide & Vehicle Allocation",
                isNew ? "CREATE_GUIDE" : "UPDATE_GUIDE",
                "Guide record saved: " + saved.getFullName() + " (License: " + saved.getLicenseNumber() + ")"
        );
        return saved;
    }

    // Vehicle operations
    public List<Vehicle> getAllVehicles() { return vehicleRepository.findAll(); }
    public List<Vehicle> getAvailableVehicles() { return vehicleRepository.findByStatus("AVAILABLE"); }
    public Optional<Vehicle> findVehicleById(Long id) { return vehicleRepository.findById(id); }

    @Transactional
    public Vehicle saveVehicle(Vehicle vehicle, String actorEmail) {
        boolean isNew = (vehicle.getId() == null);
        Vehicle saved = vehicleRepository.save(vehicle);
        activityLogService.publishActivity(
                actorEmail,
                "OPERATIONS_MANAGER",
                "Guide & Vehicle Allocation",
                isNew ? "CREATE_VEHICLE" : "UPDATE_VEHICLE",
                "Vehicle record saved: " + saved.getRegistrationNumber() + " (" + saved.getVehicleModel() + ")"
        );
        return saved;
    }

    @Transactional
    public void setVehicleMaintenance(Long vehicleId, boolean underMaintenance, String actorEmail) {
        vehicleRepository.findById(vehicleId).ifPresent(v -> {
            v.setStatus(underMaintenance ? "MAINTENANCE" : "AVAILABLE");
            v.setConditionStatus(underMaintenance ? "UNDER_MAINTENANCE" : "GOOD");
            vehicleRepository.save(v);
            activityLogService.publishActivity(
                    actorEmail,
                    "OPERATIONS_MANAGER",
                    "Guide & Vehicle Allocation",
                    "VEHICLE_MAINTENANCE",
                    "Vehicle " + v.getRegistrationNumber() + " set to " + v.getStatus()
            );
        });
    }

    // Allocation operations & Conflict Detection Engine
    public List<TripAllocation> getAllAllocations() {
        return allocationRepository.findAllByOrderByAllocationDateDesc();
    }

    public Optional<TripAllocation> findAllocationById(Long id) {
        return allocationRepository.findById(id);
    }

    public Optional<TripAllocation> findAllocationByBookingId(Long bookingId) {
        return allocationRepository.findByBookingId(bookingId);
    }

    @Transactional
    public TripAllocation assignCrew(Long bookingId, Long guideId, Long vehicleId, String dispatchNotes, String actorEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new IllegalArgumentException("Guide not found"));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        LocalDate tripDate = booking.getTripDate();

        Optional<TripAllocation> existingOpt = allocationRepository.findByBookingId(bookingId);
        Long excludeAllocationId = existingOpt.map(TripAllocation::getId).orElse(null);

        // 1. Conflict Detection: Is guide already busy on this date?
        if (allocationRepository.isGuideBusyOnDate(guideId, tripDate, excludeAllocationId)) {
            throw new IllegalStateException("Allocation Conflict: Guide " + guide.getFullName() +
                    " is already assigned to another safari trip on " + tripDate + "!");
        }

        // 2. Conflict Detection: Is vehicle already assigned on this date?
        if (allocationRepository.isVehicleBusyOnDate(vehicleId, tripDate, excludeAllocationId)) {
            throw new IllegalStateException("Allocation Conflict: Vehicle " + vehicle.getRegistrationNumber() +
                    " is already allocated to another safari trip on " + tripDate + "!");
        }

        // 3. Vehicle Capacity check
        if (vehicle.getCapacity() < booking.getParticipantCount()) {
            throw new IllegalArgumentException("Vehicle capacity (" + vehicle.getCapacity() +
                    ") is insufficient for booking participant count (" + booking.getParticipantCount() + ").");
        }

        // 4. Vehicle maintenance check
        if ("MAINTENANCE".equalsIgnoreCase(vehicle.getStatus())) {
            throw new IllegalStateException("Vehicle " + vehicle.getRegistrationNumber() + " is currently under maintenance and cannot be dispatched.");
        }

        TripAllocation allocation = existingOpt.orElseGet(() -> new TripAllocation(booking, guide, vehicle, tripDate, dispatchNotes));
        allocation.setGuide(guide);
        allocation.setVehicle(vehicle);
        allocation.setAllocationDate(tripDate);
        allocation.setDispatchNotes(dispatchNotes);
        allocation.setStatus("ASSIGNED");

        TripAllocation saved = allocationRepository.save(allocation);

        // Update booking status to confirmed if pending
        if ("PENDING".equalsIgnoreCase(booking.getBookingStatus())) {
            booking.setBookingStatus("CONFIRMED");
            bookingRepository.save(booking);
        }

        activityLogService.publishActivity(
                actorEmail,
                "OPERATIONS_MANAGER",
                "Guide & Vehicle Allocation",
                "CREW_ASSIGNED",
                "Booking " + booking.getBookingReference() + " assigned Guide " + guide.getFullName() +
                        " and Vehicle " + vehicle.getRegistrationNumber()
        );

        // Notify the guide
        notificationService.send(
                guide.getEmail(),
                "New Safari Assignment 🌿",
                "You have been assigned to Booking " + booking.getBookingReference() +
                " on " + tripDate + ". Package: " + booking.getSafariPackage().getName() +
                ". Participants: " + booking.getParticipantCount() + ". Vehicle: " + vehicle.getRegistrationNumber(),
                "SUCCESS"
        );

        // Notify the tourist / customer
        if (booking.getCustomerEmail() != null) {
            notificationService.send(
                    booking.getCustomerEmail(),
                    "Safari Crew Assigned: Guide & Cruiser Confirmed 🚙🌿",
                    "Your safari expedition (" + booking.getBookingReference() + ") on " + tripDate +
                            " has been assigned dedicated naturalist guide " + guide.getFullName() +
                            " (Contact: " + guide.getContactNumber() + ") and 4x4 Safari Cruiser " +
                            vehicle.getRegistrationNumber() + " (" + vehicle.getVehicleModel() + ").",
                    "CREW_ASSIGNED"
            );
        }

        return saved;
    }

    @Transactional
    public TripAllocation reassignCrew(Long allocationId, Long newGuideId, Long newVehicleId, String newNotes, String actorEmail) {
        TripAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found"));

        Guide newGuide = guideRepository.findById(newGuideId)
                .orElseThrow(() -> new IllegalArgumentException("Guide not found"));

        Vehicle newVehicle = vehicleRepository.findById(newVehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        LocalDate date = allocation.getAllocationDate();

        // Conflict check excluding current allocation
        if (allocationRepository.isGuideBusyOnDate(newGuideId, date, allocationId)) {
            throw new IllegalStateException("Conflict: Guide " + newGuide.getFullName() + " is already booked on " + date);
        }
        if (allocationRepository.isVehicleBusyOnDate(newVehicleId, date, allocationId)) {
            throw new IllegalStateException("Conflict: Vehicle " + newVehicle.getRegistrationNumber() + " is already booked on " + date);
        }

        allocation.setGuide(newGuide);
        allocation.setVehicle(newVehicle);
        allocation.setDispatchNotes(newNotes);
        allocation.setStatus("REASSIGNED");

        TripAllocation saved = allocationRepository.save(allocation);

        activityLogService.publishActivity(
                actorEmail,
                "OPERATIONS_MANAGER",
                "Guide & Vehicle Allocation",
                "CREW_REASSIGNED",
                "Reassigned crew for booking " + allocation.getBooking().getBookingReference() +
                        ": Guide " + newGuide.getFullName() + ", Vehicle " + newVehicle.getRegistrationNumber()
        );

        // Notify guide & tourist of reassignment
        notificationService.send(
                newGuide.getEmail(),
                "Updated Safari Assignment 🌿",
                "You have been reassigned to Booking " + allocation.getBooking().getBookingReference() + " on " + date,
                "INFO"
        );

        if (allocation.getBooking() != null && allocation.getBooking().getCustomerEmail() != null) {
            notificationService.send(
                    allocation.getBooking().getCustomerEmail(),
                    "Safari Crew Update: Guide & Cruiser Reassigned 🚙",
                    "Your safari crew for booking " + allocation.getBooking().getBookingReference() +
                            " has been updated to Guide " + newGuide.getFullName() + " (Contact: " + newGuide.getContactNumber() +
                            ") and Vehicle " + newVehicle.getRegistrationNumber() + " (" + newVehicle.getVehicleModel() + ").",
                    "CREW_ASSIGNED"
            );
        }

        return saved;
    }

    @Transactional
    public void removeAllocation(Long allocationId, String actorEmail) {
        allocationRepository.findById(allocationId).ifPresent(alloc -> {
            activityLogService.publishActivity(
                    actorEmail,
                    "OPERATIONS_MANAGER",
                    "Guide & Vehicle Allocation",
                    "REMOVE_ALLOCATION",
                    "Allocation for " + alloc.getBooking().getBookingReference() + " removed."
            );
            allocationRepository.delete(alloc);
        });
    }

    // ─── Guide Availability Management ───────────────────────────────────
    public List<GuideAvailability> getGuideAvailabilityByEmail(String email) {
        return availabilityRepository.findByGuideEmail(email);
    }

    public List<GuideAvailability> getGuideAvailabilityById(Long guideId) {
        return availabilityRepository.findByGuideIdOrderByUnavailableDateDesc(guideId);
    }

    @Transactional
    public void markUnavailable(Long guideId, LocalDate date, String reason) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new IllegalArgumentException("Guide not found"));
        // Skip if already marked
        if (availabilityRepository.findByGuideIdAndDate(guideId, date).isPresent()) return;
        availabilityRepository.save(new GuideAvailability(guide, date, reason));
    }

    @Transactional
    public void removeUnavailableDate(Long availabilityId) {
        availabilityRepository.deleteById(availabilityId);
    }

    public Optional<Guide> findGuideByEmail(String email) {
        return guideRepository.findByEmail(email);
    }

    public List<TripAllocation> getAllocationsByGuideEmail(String email) {
        return allocationRepository.findByGuideEmailOrderByAllocationDateDesc(email);
    }

    // ─── Guide Management CRUD ───────────────────────────────────────────
    @Transactional
    public Guide updateGuide(Long guideId, Guide updated, String actorEmail) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new IllegalArgumentException("Guide not found with id: " + guideId));
        guide.setFullName(updated.getFullName());
        guide.setLicenseNumber(updated.getLicenseNumber());
        guide.setContactNumber(updated.getContactNumber());
        guide.setEmail(updated.getEmail());
        guide.setLanguages(updated.getLanguages());
        guide.setExperienceYears(updated.getExperienceYears());
        guide.setDailyRate(updated.getDailyRate());
        guide.setStatus(updated.getStatus());
        Guide saved = guideRepository.save(guide);

        activityLogService.publishActivity(
                actorEmail,
                "OPERATIONS_MANAGER",
                "Guide & Vehicle Allocation",
                "UPDATE_GUIDE",
                "Updated guide profile: " + saved.getFullName() + " (License: " + saved.getLicenseNumber() + ")"
        );
        return saved;
    }

    @Transactional
    public void deleteGuide(Long guideId, String actorEmail) {
        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new IllegalArgumentException("Guide not found with id: " + guideId));

        long activeTrips = allocationRepository.countActiveAllocationsByGuideId(guideId);
        if (activeTrips > 0) {
            throw new IllegalStateException("Cannot delete Guide " + guide.getFullName() +
                    ": Guide is currently assigned to " + activeTrips + " active/upcoming safari trips.");
        }

        if (guide.getEmail() != null) {
            List<GuideAvailability> unavail = availabilityRepository.findByGuideEmail(guide.getEmail());
            availabilityRepository.deleteAll(unavail);
        }

        guideRepository.delete(guide);

        activityLogService.publishActivity(
                actorEmail,
                "OPERATIONS_MANAGER",
                "Guide & Vehicle Allocation",
                "DELETE_GUIDE",
                "Deleted guide record: " + guide.getFullName() + " (License: " + guide.getLicenseNumber() + ")"
        );
    }

    // ─── 4x4 Cruiser / Vehicle CRUD ──────────────────────────────────────
    @Transactional
    public Vehicle updateVehicle(Long vehicleId, Vehicle updated, String actorEmail) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + vehicleId));
        vehicle.setRegistrationNumber(updated.getRegistrationNumber());
        vehicle.setVehicleModel(updated.getVehicleModel());
        vehicle.setCapacity(updated.getCapacity());
        vehicle.setConditionStatus(updated.getConditionStatus());
        vehicle.setStatus(updated.getStatus());
        vehicle.setLastServiceDate(updated.getLastServiceDate());
        Vehicle saved = vehicleRepository.save(vehicle);

        activityLogService.publishActivity(
                actorEmail,
                "OPERATIONS_MANAGER",
                "Guide & Vehicle Allocation",
                "UPDATE_VEHICLE",
                "Updated vehicle: " + saved.getRegistrationNumber() + " (" + saved.getVehicleModel() + ")"
        );
        return saved;
    }

    @Transactional
    public void deleteVehicle(Long vehicleId, String actorEmail) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found with id: " + vehicleId));

        long activeTrips = allocationRepository.countActiveAllocationsByVehicleId(vehicleId);
        if (activeTrips > 0) {
            throw new IllegalStateException("Cannot delete 4x4 Cruiser " + vehicle.getRegistrationNumber() +
                    ": Vehicle is currently assigned to " + activeTrips + " active/upcoming safari trips.");
        }

        vehicleRepository.delete(vehicle);

        activityLogService.publishActivity(
                actorEmail,
                "OPERATIONS_MANAGER",
                "Guide & Vehicle Allocation",
                "DELETE_VEHICLE",
                "Deleted cruiser record: " + vehicle.getRegistrationNumber() + " (" + vehicle.getVehicleModel() + ")"
        );
    }
}
