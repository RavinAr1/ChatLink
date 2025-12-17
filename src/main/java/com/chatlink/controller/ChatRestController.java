package com.chatlink.controller;

import com.chatlink.model.ChatMessage;
import com.chatlink.model.ChatAttachment;
import com.chatlink.service.ChatService;
import com.chatlink.service.ChatAttachmentService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;
    private final ChatAttachmentService chatAttachmentService;

    @Value("${upload.dir}")     // Directory for file uploads
    private String uploadDir;

    @GetMapping("/history")     // Get chat history between two users
    public List<Object> getChatHistory(@RequestParam Long user1, @RequestParam Long user2) {
        List<Object> combined = new ArrayList<>();
        combined.addAll(chatService.getChatHistory(user1, user2));
        combined.addAll(chatAttachmentService.getAttachments(user1, user2));

        combined.sort(Comparator.comparing(o ->
                o instanceof ChatMessage
                        ? ((ChatMessage) o).getTimestamp()
                        : ((ChatAttachment) o).getTimestamp()
        ));

        return combined;
    }

    @PostMapping("/upload")     // Upload a file attachment
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("receiverId") Long receiverId,
                                        @RequestParam(required = false) Long senderId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        if (receiverId == null) {
            return ResponseEntity.badRequest().body("receiverId is required");
        }

        try {
            // Ensure upload directory exists
            File dir = new File(uploadDir);
            if (!dir.exists() && !dir.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)  // If the directory does not exist and cannot be created,

                        .body("Could not create upload directory");
            }

            // Safe filename
            String originalFilename = file.getOriginalFilename(); // Get original filename
            String safeFilename = System.currentTimeMillis() + "_" +
                    originalFilename.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"); // Sanitize filename

            Path destPath = Path.of(dir.getAbsolutePath(), safeFilename);   // Destination path
            Files.copy(file.getInputStream(), destPath, StandardCopyOption.REPLACE_EXISTING);   // Save file

            // Save attachment record
            ChatAttachment attachment = new ChatAttachment();
            attachment.setFileName(originalFilename);
            attachment.setFileType(file.getContentType());
            attachment.setFileUrl("/api/chat/download/" + safeFilename);    // URL to download the file
            attachment.setReceiverId(receiverId);
            if (senderId != null) attachment.setSenderId(senderId);
            attachment.setTimestamp(Instant.now());

            chatAttachmentService.saveAttachment(attachment);
            return ResponseEntity.ok(attachment);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed");
        }
    }

    // ===================== FILE DOWNLOAD =====================
    @GetMapping("/download/{fileName:.+}")      // Download a file attachment
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        File file = new File(uploadDir, fileName);
        if (!file.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new FileSystemResource(file));
    }

    @DeleteMapping("/message/{id}")     // Delete a chat message
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        chatService.deleteMessage(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/attachment/{id}")      // Delete a chat attachment
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        chatAttachmentService.deleteAttachment(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")   // Delete entire chat between two users
    public ResponseEntity<Void> deleteChat(@RequestParam Long user1,
                                           @RequestParam Long user2) {
        chatService.deleteChat(user1, user2);
        return ResponseEntity.ok().build();
    }
}
