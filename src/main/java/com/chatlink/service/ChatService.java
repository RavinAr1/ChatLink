package com.chatlink.service;

import com.chatlink.model.ChatMessage;
import com.chatlink.model.ChatAttachment;
import java.util.List;
import java.util.Optional;

public interface ChatService {

    ChatMessage saveMessage(ChatMessage message); // Save a chat message

    List<ChatMessage> getChatHistory(Long user1, Long user2); // Get messages between two users


    void deleteMessage(Long messageId); // Delete a single message

    void deleteChat(Long user1, Long user2); // Delete all messages between two users

}
