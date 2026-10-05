package com.safari.module.inventory_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentAllocationRepository extends JpaRepository<EquipmentAllocation, Long> {
    List<EquipmentAllocation> findByBookingId(Long bookingId);
    List<EquipmentAllocation> findByStatus(String status);
    java.util.Optional<EquipmentAllocation> findByEquipmentIdAndBookingId(Long equipmentId, Long bookingId);

    @Query("SELECT COUNT(a) > 0 FROM EquipmentAllocation a WHERE a.equipment.id = :equipmentId AND a.status IN ('ISSUED', 'REQUESTED')")
    boolean hasActiveAllocations(Long equipmentId);
}
