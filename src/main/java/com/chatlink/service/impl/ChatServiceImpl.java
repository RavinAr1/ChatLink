package com.chatlink.service.impl;

import com.chatlink.model.ChatMessage;
import com.chatlink.model.ChatAttachment;
import com.chatlink.repository.ChatMessageRepository;
import com.chatlink.service.ChatService;
import com.chatlink.service.ChatAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatRepo;
    private final ChatAttachmentService attachmentService;

    @Override
    public ChatMessage saveMessage(ChatMessage message) {
        return chatRepo.save(message); // Persist chat message
    }

    @Override
    public List<ChatMessage> getChatHistory(Long user1, Long user2) {
        return chatRepo.findChatHistory(user1, user2); // Fetch messages between users
    }

//    @Override
//    public List<Object> getFullChatHistory(Long user1, Long user2) {
//        List<Object> combined = new ArrayList<>();
//
//        combined.addAll(chatRepo.findChatHistory(user1, user2));
//        combined.addAll(attachmentService.getAttachments(user1, user2)); // Add attachments
//
//        combined.sort(Comparator.comparing(o -> {
//            if (o instanceof ChatMessage) return ((ChatMessage) o).getTimestamp();
//            else return ((ChatAttachment) o).getTimestamp();
//        })); // Sort everything by timestamp
//
//        return combined;
//    }
}
