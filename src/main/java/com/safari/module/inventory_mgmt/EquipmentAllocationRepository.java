package com.safari.module.inventory_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentAllocationRepository extends JpaRepository<EquipmentAllocation, Long> {
    List<EquipmentAllocation> findByBookingId(Long bookingId);
    List<EquipmentAllocation> findByStatus(String status);
}
