package com.kennel.mart.kennelmart.repository;

import com.kennel.mart.kennelmart.entity.Message;
import com.kennel.mart.kennelmart.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    // Find conversation between two users (paginated, most recent first)
    Page<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByCreatedAtDesc(
            User sender1, User receiver1, User sender2, User receiver2, Pageable pageable);

    // Find all messages where the user is either sender or receiver (for inbox)
    List<Message> findAllBySenderOrReceiver(User sender, User receiver);

    // Count unread messages for a user
    int countByReceiverAndReadFalse(User receiver);

    // Mark messages as read (for a specific sender)
    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.receiver = :receiver AND m.sender = :sender AND m.read = false")
    int markAsRead(@Param("receiver") User receiver, @Param("sender") User sender);
}