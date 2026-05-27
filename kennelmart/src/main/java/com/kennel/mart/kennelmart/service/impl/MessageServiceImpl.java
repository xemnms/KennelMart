package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.dto.MessageRequest;
import com.kennel.mart.kennelmart.dto.MessageResponse;
import com.kennel.mart.kennelmart.entity.Message;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.repository.MessageRepository;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.service.MessageService;
import com.kennel.mart.kennelmart.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MessageServiceImpl.class);

    public MessageServiceImpl(MessageRepository messageRepository, UserRepository userRepository, NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public MessageResponse sendMessage(String senderEmail, MessageRequest request) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Message message = new Message(sender, receiver, request.getContent());
        Message saved = messageRepository.save(message);

        // Send notification to receiver
        String contentPreview = request.getContent().length() > 100 ? request.getContent().substring(0, 100) + "..." : request.getContent();
        notificationService.sendNotification(
                receiver.getId(),
                "New message from " + sender.getName(),
                contentPreview,
                "MESSAGE",
                saved.getId().toString()
        );

        log.info("Message sent from {} to {}", sender.getEmail(), receiver.getEmail());
        return convertToResponse(saved);
    }

    @Override
    public Page<MessageResponse> getConversation(String userEmail, UUID otherUserId, Pageable pageable) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("Other user not found"));

        Page<Message> messages = messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByCreatedAtDesc(
                currentUser, otherUser, currentUser, otherUser, pageable);

        return messages.map(this::convertToResponse);
    }

    @Override
    public int getUnreadCount(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return messageRepository.countByReceiverAndReadFalse(user);
    }

    @Override
    public void markMessagesAsRead(String userEmail, UUID otherUserId) {
        User currentUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("Other user not found"));

        int updated = messageRepository.markAsRead(currentUser, otherUser);
        if (updated > 0) {
            log.info("Marked {} messages as read from {} to {}", updated, otherUser.getEmail(), currentUser.getEmail());
        }
    }

    private MessageResponse convertToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getName())
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getName())
                .content(message.getContent())
                .read(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}