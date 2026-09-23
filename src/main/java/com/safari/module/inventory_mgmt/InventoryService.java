package com.safari.module.inventory_mgmt;

import com.safari.common.ActivityLogService;
import com.safari.module.booking_mgmt.Booking;
import com.safari.module.booking_mgmt.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentAllocationRepository allocationRepository;
    private final BookingRepository bookingRepository;
    private final ActivityLogService activityLogService;

    public InventoryService(EquipmentRepository equipmentRepository,
                            EquipmentAllocationRepository allocationRepository,
                            BookingRepository bookingRepository,
                            ActivityLogService activityLogService) {
        this.equipmentRepository = equipmentRepository;
        this.allocationRepository = allocationRepository;
        this.bookingRepository = bookingRepository;
        this.activityLogService = activityLogService;
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAllByOrderByItemNameAsc();
    }

    public List<Equipment> getLowStockEquipment() {
        return equipmentRepository.findLowStockItems();
    }

    public Optional<Equipment> findEquipmentById(Long id) {
        return equipmentRepository.findById(id);
    }

    public List<EquipmentAllocation> getAllocationsForBooking(Long bookingId) {
        return allocationRepository.findByBookingId(bookingId);
    }

    public List<EquipmentAllocation> getPendingRequests() {
        return allocationRepository.findByStatus("REQUESTED");
    }

    public List<EquipmentAllocation> getActiveAllocations() {
        return allocationRepository.findByStatus("ISSUED");
    }

    @Transactional
    public Equipment saveEquipment(Equipment equipment, String actorEmail) {
        boolean isNew = (equipment.getId() == null);
        if (isNew) {
            equipment.setAvailableQuantity(equipment.getTotalQuantity());
        }
        Equipment saved = equipmentRepository.save(equipment);
        activityLogService.publishActivity(
                actorEmail,
                "LOGISTICS_STAFF",
                "Inventory & Equipment",
                isNew ? "ADD_EQUIPMENT" : "UPDATE_EQUIPMENT",
                "Equipment item: " + saved.getItemName() + " (Code: " + saved.getItemCode() + ", Total: " + saved.getTotalQuantity() + ")"
        );
        return saved;
    }

    @Transactional
    public EquipmentAllocation issueEquipmentToBooking(Long equipmentId, Long bookingId, int quantity, String remarks, String actorEmail) {
        Equipment eq = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment item not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (eq.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Insufficient inventory: Only " + eq.getAvailableQuantity() +
                    " units available for " + eq.getItemName());
        }

        // Deduct available stock
        eq.setAvailableQuantity(eq.getAvailableQuantity() - quantity);
        equipmentRepository.save(eq);

        EquipmentAllocation allocation = new EquipmentAllocation(eq, booking, quantity, LocalDate.now(), remarks);
        EquipmentAllocation saved = allocationRepository.save(allocation);

        activityLogService.publishActivity(
                actorEmail,
                "LOGISTICS_STAFF",
                "Inventory & Equipment",
                "ISSUE_EQUIPMENT",
                "Issued " + quantity + "x " + eq.getItemName() + " to booking " + booking.getBookingReference()
        );

        return saved;
    }

    @Transactional
    public EquipmentAllocation requestEquipment(Long equipmentId, Long bookingId, int quantity, String remarks, String actorEmail) {
        Equipment eq = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment item not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        EquipmentAllocation allocation = new EquipmentAllocation(eq, booking, quantity, LocalDate.now(), remarks);
        allocation.setStatus("REQUESTED");
        EquipmentAllocation saved = allocationRepository.save(allocation);

        activityLogService.publishActivity(
                actorEmail,
                "CUSTOMER",
                "Inventory & Equipment",
                "REQUEST_EQUIPMENT",
                "Requested " + quantity + "x " + eq.getItemName() + " for booking " + booking.getBookingReference()
        );

        return saved;
    }

    @Transactional
    public EquipmentAllocation approveEquipmentRequest(Long allocationId, String actorEmail) {
        EquipmentAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found"));
        
        if (!"REQUESTED".equals(allocation.getStatus())) {
            throw new IllegalStateException("Allocation is not in REQUESTED status");
        }

        Equipment eq = allocation.getEquipment();
        if (eq.getAvailableQuantity() < allocation.getAllocatedQuantity()) {
            throw new IllegalStateException("Insufficient inventory: Only " + eq.getAvailableQuantity() +
                    " units available for " + eq.getItemName());
        }

        eq.setAvailableQuantity(eq.getAvailableQuantity() - allocation.getAllocatedQuantity());
        equipmentRepository.save(eq);

        allocation.setStatus("ISSUED");
        allocation.setIssuedDate(LocalDate.now());
        EquipmentAllocation saved = allocationRepository.save(allocation);

        activityLogService.publishActivity(
                actorEmail,
                "LOGISTICS_STAFF",
                "Inventory & Equipment",
                "APPROVE_EQUIPMENT",
                "Approved " + allocation.getAllocatedQuantity() + "x " + eq.getItemName() + " to booking " + allocation.getBooking().getBookingReference()
        );

        return saved;
    }

    @Transactional
    public void returnEquipment(Long allocationId, String conditionReport, String actorEmail) {
        EquipmentAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Equipment allocation record not found"));

        allocation.setStatus("RETURNED");
        allocation.setReturnDate(LocalDate.now());
        allocation.setRemarks(allocation.getRemarks() + " | Return condition: " + conditionReport);

        Equipment eq = allocation.getEquipment();
        eq.setAvailableQuantity(eq.getAvailableQuantity() + allocation.getAllocatedQuantity());
        equipmentRepository.save(eq);
        allocationRepository.save(allocation);

        activityLogService.publishActivity(
                actorEmail,
                "LOGISTICS_STAFF",
                "Inventory & Equipment",
                "RETURN_EQUIPMENT",
                "Returned " + allocation.getAllocatedQuantity() + "x " + eq.getItemName() +
                        " from booking " + allocation.getBooking().getBookingReference()
        );
    }

    @Transactional
    public void deleteEquipment(Long id, String actorEmail) {
        equipmentRepository.findById(id).ifPresent(eq -> {
            activityLogService.publishActivity(
                    actorEmail,
                    "LOGISTICS_STAFF",
                    "Inventory & Equipment",
                    "DECOMMISSION_EQUIPMENT",
                    "Decommissioned equipment: " + eq.getItemName() + " (" + eq.getItemCode() + ")"
            );
            equipmentRepository.delete(eq);
        });
    }
}
