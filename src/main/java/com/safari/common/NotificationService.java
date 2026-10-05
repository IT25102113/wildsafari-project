package com.safari.common;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void send(String recipientEmail, String title, String message, String type) {
        notificationRepository.save(new Notification(recipientEmail, title, message, type));
    }

    @Transactional
    public void sendNotification(String recipientEmail, String title, String message, String type) {
        send(recipientEmail, title, message, type);
    }

    public List<Notification> getAll(String email) {
        return notificationRepository.findByRecipientEmailOrderByCreatedAtDesc(email);
    }

    public List<Notification> getUnread(String email) {
        return notificationRepository.findByRecipientEmailAndIsReadFalseOrderByCreatedAtDesc(email);
    }

    public long countUnread(String email) {
        return notificationRepository.countByRecipientEmailAndIsReadFalse(email);
    }

    @Transactional
    public void markRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllRead(String email) {
        List<Notification> unread = notificationRepository
                .findByRecipientEmailAndIsReadFalseOrderByCreatedAtDesc(email);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
