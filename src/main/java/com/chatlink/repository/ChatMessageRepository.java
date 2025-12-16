package com.chatlink.repository;

import com.chatlink.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m " +
            "WHERE (m.senderId = :user1 AND m.receiverId = :user2) " +
            "   OR (m.senderId = :user2 AND m.receiverId = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findChatHistory(@Param("user1") Long user1,
                                      @Param("user2") Long user2); // Get all messages between two users in time order



    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE (m.senderId = :u1 AND m.receiverId = :u2) OR (m.senderId = :u2 AND m.receiverId = :u1)")
    void deleteChatBetween(@Param("u1") Long user1, @Param("u2") Long user2);


}
