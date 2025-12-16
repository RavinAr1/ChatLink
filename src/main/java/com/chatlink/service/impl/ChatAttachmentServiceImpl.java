package com.chatlink.service.impl;

import com.chatlink.model.ChatAttachment;
import com.chatlink.repository.ChatAttachmentRepository;
import com.chatlink.service.ChatAttachmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatAttachmentServiceImpl implements ChatAttachmentService {

    private final ChatAttachmentRepository attachmentRepo;



    @Override

    public ChatAttachment saveAttachment(ChatAttachment attachment) {   // save or update attachment
        if (attachment.getTimestamp() == null) {
            attachment.setTimestamp(LocalDateTime.now());
        }
        return attachmentRepo.save(attachment);
    }

    @Override
    public List<ChatAttachment> getAttachments(Long user1, Long user2) {    // get all attachments between two users
        return attachmentRepo.findChatAttachments(user1, user2);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId) {    // delete attachment by id
        attachmentRepo.deleteById(attachmentId);
    }

    @Override
    @Transactional
    public void deleteChatAttachments(Long user1, Long user2) {     // delete all attachments between two users
        attachmentRepo.deleteAttachmentsBetween(user1, user2);
    }

}
