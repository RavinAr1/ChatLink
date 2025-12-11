package com.chatlink.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_attachments")
public class ChatAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;   // IMAGE, FILE, etc.

    @Column(nullable = false)
    private String fileUrl;    // Path to stored file

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
