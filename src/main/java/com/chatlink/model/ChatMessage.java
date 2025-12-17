package com.chatlink.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;


@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;
    private String content;
    private Instant timestamp;

    private Long replyToMessageId;
    private String replyPreview;


}
