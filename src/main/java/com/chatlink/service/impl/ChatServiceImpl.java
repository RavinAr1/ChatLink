package com.chatlink.service.impl;

import com.chatlink.model.ChatMessage;
import com.chatlink.repository.ChatMessageRepository;
import com.chatlink.service.ChatAttachmentService;
import com.chatlink.service.ChatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatRepo;
    private final ChatAttachmentService attachmentService;


    @Override
    public ChatMessage saveMessage(ChatMessage message) {       // save message to the database
        return chatRepo.save(message);
    }

    @Override
    public List<ChatMessage> getChatHistory(Long user1, Long user2) {       // retrieve chat history between two users
        return chatRepo.findChatHistory(user1, user2);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {     // delete a specific message by its ID
        chatRepo.deleteById(messageId);
    }

    @Override
    @Transactional
    public void deleteChat(Long user1, Long user2) {        // delete entire chat history between two users
        chatRepo.deleteChatBetween(user1, user2);
        attachmentService.deleteChatAttachments(user1, user2);
    }

}
