package com.safari.module.package_mgmt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SafariPackageRepository extends JpaRepository<SafariPackage, Long> {
    List<SafariPackage> findByStatus(String status);
    List<SafariPackage> findByNationalParkAndStatus(String nationalPark, String status);

    @Query("SELECT p FROM SafariPackage p WHERE (:park IS NULL OR p.nationalPark = :park) AND (:status IS NULL OR p.status = :status)")
    List<SafariPackage> searchPackages(String park, String status);
}
