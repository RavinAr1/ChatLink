package com.chatlink.controller;

import com.chatlink.model.ChatAttachment;
import com.chatlink.model.ChatMessage;
import com.chatlink.service.ChatService;
import com.chatlink.service.ChatAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


import java.time.Instant;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatAttachmentService chatAttachmentService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat.send")   // for sending messages
    public void sendMessage(ChatMessage message) {
        message.setTimestamp(Instant.now());
        // Save replyToMessageId if any
        chatService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/messages", message);
    }


    @MessageMapping("/chat.send-file")  //  for sending files
    public void sendFile(ChatAttachment attachment) {
        attachment.setTimestamp(Instant.now());
        chatAttachmentService.saveAttachment(attachment);
        messagingTemplate.convertAndSend("/topic/files", attachment);
    }




}
