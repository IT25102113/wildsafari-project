package com.safari.module.user_mgmt;

import com.safari.common.ActivityLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Value("${safari.upload.dir:uploads}")
    private String uploadDir;

    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;

    public UserService(UserRepository userRepository, ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
    }

    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            activityLogService.publishActivity(
                    email,
                    userOpt.get().getRole(),
                    "Authentication",
                    "LOGIN_SUCCESS",
                    "User " + userOpt.get().getFullName() + " successfully signed in."
            );
            return userOpt;
        }
        return Optional.empty();
    }

    @Transactional
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("An account with email " + user.getEmail() + " already exists.");
        }
        User saved = userRepository.save(user);
        activityLogService.publishActivity(
                saved.getEmail(),
                saved.getRole(),
                "User Management",
                "REGISTER",
                "New account created for " + saved.getFullName() + " with role " + saved.getRole()
        );
        return saved;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateProfile(Long userId, String fullName, String phone, String region, String bio,
                              String newPassword, MultipartFile profilePictureFile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName.trim());
        }
        if (phone != null && !phone.isBlank()) {
            user.setPhone(phone.trim());
        }
        user.setRegion(region != null ? region.trim() : null);
        user.setBio(bio != null ? bio.trim() : null);

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(newPassword.trim());
        }

        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                String originalFilename = profilePictureFile.getOriginalFilename();
                String ext = (originalFilename != null && originalFilename.contains("."))
                        ? originalFilename.substring(originalFilename.lastIndexOf("."))
                        : ".jpg";
                String fileName = "avatar_" + user.getId() + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
                Path destination = uploadPath.resolve(fileName);
                Files.copy(profilePictureFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                user.setProfilePicture("/uploads/" + fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to store profile picture: " + e.getMessage(), e);
            }
        }

        User saved = userRepository.save(user);
        activityLogService.publishActivity(
                saved.getEmail(),
                saved.getRole(),
                "Profile Management",
                "PROFILE_UPDATE",
                "User " + saved.getFullName() + " updated their profile details."
        );
        return saved;
    }

    @Transactional
    public void deleteUser(Long id, String operatorEmail) {
        userRepository.findById(id).ifPresent(user -> {
            activityLogService.publishActivity(
                    operatorEmail,
                    "ADMIN",
                    "User Management",
                    "DELETE_USER",
                    "User account removed: " + user.getEmail()
            );
            userRepository.delete(user);
        });
    }
}
