package com.chatlink.controller;

import com.chatlink.model.ChatAttachment;
import com.chatlink.model.ChatMessage;
import com.chatlink.service.ChatService;
import com.chatlink.service.ChatAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatAttachmentService chatAttachmentService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat.send")   // for sending messages
    public void sendMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        // Save replyToMessageId if any
        chatService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/messages", message);
    }


    @MessageMapping("/chat.send-file")  //  for sending files
    public void sendFile(ChatAttachment attachment) {
        attachment.setTimestamp(LocalDateTime.now());
        chatAttachmentService.saveAttachment(attachment);
        messagingTemplate.convertAndSend("/topic/files", attachment);
    }




}
