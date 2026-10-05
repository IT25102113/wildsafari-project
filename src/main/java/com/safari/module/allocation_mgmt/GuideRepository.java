package com.safari.module.allocation_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Long> {
    List<Guide> findByStatus(String status);
    Optional<Guide> findByEmail(String email);
}
