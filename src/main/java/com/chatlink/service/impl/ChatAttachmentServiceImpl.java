package com.chatlink.service.impl;

import com.chatlink.model.ChatAttachment;
import com.chatlink.repository.ChatAttachmentRepository;
import com.chatlink.service.ChatAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatAttachmentServiceImpl implements ChatAttachmentService {

    private final ChatAttachmentRepository attachmentRepo;

    @Override
    public ChatAttachment saveAttachment(ChatAttachment attachment) {
        if (attachment.getTimestamp() == null) {
            attachment.setTimestamp(LocalDateTime.now()); // Set timestamp if missing
        }
        return attachmentRepo.save(attachment); // Persist attachment
    }

    @Override
    public List<ChatAttachment> getAttachments(Long user1, Long user2) {
        return attachmentRepo.findChatAttachments(user1, user2); // Fetch attachments
    }
}
