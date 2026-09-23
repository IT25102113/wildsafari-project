package com.safari.module.package_mgmt;

import com.safari.common.ActivityLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SafariPackageService {

    private final SafariPackageRepository packageRepository;
    private final ActivityLogService activityLogService;

    @Value("${safari.upload.dir:uploads}")
    private String uploadDir;

    public SafariPackageService(SafariPackageRepository packageRepository, ActivityLogService activityLogService) {
        this.packageRepository = packageRepository;
        this.activityLogService = activityLogService;
    }

    public List<SafariPackage> getAllPackages() {
        return packageRepository.findAll();
    }

    public List<SafariPackage> getActivePackages() {
        return packageRepository.findByStatus("ACTIVE");
    }

    public Optional<SafariPackage> findById(Long id) {
        return packageRepository.findById(id);
    }

    @Transactional
    public SafariPackage savePackage(SafariPackage pkg, MultipartFile imageFile, String actorEmail) {
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            pkg.setCoverImage("/uploads/" + fileName);
        } else if (pkg.getCoverImage() == null || pkg.getCoverImage().isBlank()) {
            pkg.setCoverImage("/images/default_safari.jpg");
        }

        boolean isNew = (pkg.getId() == null);
        SafariPackage saved = packageRepository.save(pkg);

        activityLogService.publishActivity(
                actorEmail,
                "TOUR_OPERATOR",
                "Safari Package Management",
                isNew ? "CREATE_PACKAGE" : "UPDATE_PACKAGE",
                "Package '" + saved.getName() + "' (" + saved.getNationalPark() + ") was " + (isNew ? "created" : "updated") + "."
        );

        return saved;
    }

    @Transactional
    public void discontinuePackage(Long id, String actorEmail) {
        packageRepository.findById(id).ifPresent(pkg -> {
            pkg.setStatus("DISCONTINUED");
            packageRepository.save(pkg);
            activityLogService.publishActivity(
                    actorEmail,
                    "TOUR_OPERATOR",
                    "Safari Package Management",
                    "DISCONTINUE_PACKAGE",
                    "Package '" + pkg.getName() + "' was discontinued to protect historical bookings."
            );
        });
    }

    @Transactional
    public void toggleStatus(Long id, String newStatus, String actorEmail) {
        packageRepository.findById(id).ifPresent(pkg -> {
            pkg.setStatus(newStatus);
            packageRepository.save(pkg);
            activityLogService.publishActivity(
                    actorEmail,
                    "TOUR_OPERATOR",
                    "Safari Package Management",
                    "TOGGLE_STATUS",
                    "Package '" + pkg.getName() + "' status changed to " + newStatus + "."
            );
        });
    }

    @Transactional
    public void deletePackage(Long id, String actorEmail) {
        packageRepository.findById(id).ifPresent(pkg -> {
            activityLogService.publishActivity(
                    actorEmail,
                    "TOUR_OPERATOR",
                    "Safari Package Management",
                    "DELETE_PACKAGE",
                    "Package '" + pkg.getName() + "' removed."
            );
            packageRepository.delete(pkg);
        });
    }

    private String saveImage(MultipartFile file) {
        try {
            Path root = Paths.get(uploadDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }
            String original = file.getOriginalFilename();
            String extension = (original != null && original.contains(".")) ? original.substring(original.lastIndexOf(".")) : ".jpg";
            String newFileName = "pkg_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Files.copy(file.getInputStream(), root.resolve(newFileName), StandardCopyOption.REPLACE_EXISTING);
            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store package image: " + e.getMessage());
        }
    }
}
