package com.safari.module.allocation_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GuideAvailabilityRepository extends JpaRepository<GuideAvailability, Long> {
    List<GuideAvailability> findByGuideIdOrderByUnavailableDateDesc(Long guideId);

    @Query("SELECT g FROM GuideAvailability g WHERE g.guide.id = :guideId AND g.unavailableDate = :date")
    Optional<GuideAvailability> findByGuideIdAndDate(Long guideId, LocalDate date);

    @Query("SELECT g FROM GuideAvailability g WHERE g.guide.email = :email ORDER BY g.unavailableDate DESC")
    List<GuideAvailability> findByGuideEmail(String email);
}
