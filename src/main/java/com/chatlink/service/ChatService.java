package com.chatlink.service;

import com.chatlink.model.ChatMessage;
import com.chatlink.model.ChatAttachment;
import java.util.List;

public interface ChatService {

    ChatMessage saveMessage(ChatMessage message); // Save a chat message

    List<ChatMessage> getChatHistory(Long user1, Long user2); // Get messages between two users

//    List<Object> getFullChatHistory(Long user1, Long user2); // Get combined messages + attachments
}
