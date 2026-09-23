package com.safari.module.inventory_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByItemCode(String itemCode);
    List<Equipment> findByCategory(String category);

    @Query("SELECT e FROM Equipment e WHERE e.availableQuantity <= e.minThreshold")
    List<Equipment> findLowStockItems();

    List<Equipment> findAllByOrderByItemNameAsc();
}
