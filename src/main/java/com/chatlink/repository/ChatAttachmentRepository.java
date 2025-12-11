package com.chatlink.repository;

import com.chatlink.model.ChatAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatAttachmentRepository extends JpaRepository<ChatAttachment, Long> {

    @Query("""
        SELECT a FROM ChatAttachment a 
        WHERE (a.senderId = :u1 AND a.receiverId = :u2)
        OR   (a.senderId = :u2 AND a.receiverId = :u1)
        ORDER BY a.timestamp ASC
    """)
    List<ChatAttachment> findChatAttachments(
            @Param("u1") Long user1,
            @Param("u2") Long user2
    ); // Fetch attachments between two users in chronological order
}
