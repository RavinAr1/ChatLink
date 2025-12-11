package com.chatlink.controller;

import com.chatlink.model.ChatAttachment;
import com.chatlink.model.ChatMessage;
import com.chatlink.service.ChatService;
import com.chatlink.service.ChatAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatAttachmentService chatAttachmentService;   // Service for file attachments
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        chatService.saveMessage(message);                         // Persist chat message
        messagingTemplate.convertAndSend("/topic/messages", message);
    }


    @MessageMapping("/chat.send-file")
    public void sendFile(ChatAttachment attachment) {
        attachment.setTimestamp(LocalDateTime.now());
        chatAttachmentService.saveAttachment(attachment);         // Persist attachment
        messagingTemplate.convertAndSend("/topic/files", attachment);
    }


    //TODO: Add methods for deleting chat messages and entire chats.

}
