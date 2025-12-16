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

    private Long senderId;
    private Long receiverId;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private LocalDateTime timestamp;


//    private Long replyToAttachmentId;
//    private String replyPreview; // store fileName
}
