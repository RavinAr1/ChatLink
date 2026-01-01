package com.chatlink.service;

import com.chatlink.model.ChatAttachment;
import java.util.List;
import java.util.Optional;

public interface ChatAttachmentService {

    ChatAttachment saveAttachment(ChatAttachment attachment); // Save an attachment

    List<ChatAttachment> getAttachments(Long user1, Long user2); // Get attachments between two users

    void deleteAttachment(Long attachmentId); // Delete single attachment

    void deleteChatAttachments(Long user1, Long user2); // Delete all attachments between users


}
