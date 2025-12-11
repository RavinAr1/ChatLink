package com.chatlink.service;

import com.chatlink.model.ChatAttachment;
import java.util.List;

public interface ChatAttachmentService {

    ChatAttachment saveAttachment(ChatAttachment attachment); // Save an attachment

    List<ChatAttachment> getAttachments(Long user1, Long user2); // Get attachments between two users
}
