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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;
    private final ChatAttachmentService chatAttachmentService;
    @Value("${upload.dir}")
    private String uploadDir;

    @GetMapping("/history")
    public List<Object> getChatHistory(@RequestParam Long user1, @RequestParam Long user2) {
        List<Object> combined = new ArrayList<>();
        combined.addAll(chatService.getChatHistory(user1, user2));
        combined.addAll(chatAttachmentService.getAttachments(user1, user2));

        combined.sort(Comparator.comparing(o -> {
            if (o instanceof ChatMessage) return ((ChatMessage) o).getTimestamp();
            else return ((ChatAttachment) o).getTimestamp();
        }));

        return combined; // Return combined sorted chat history
    }




    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("receiverId") Long receiverId,
                                        @RequestParam(required = false) Long senderId) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("File is empty");
        if (receiverId == null) return ResponseEntity.badRequest().body("receiverId is required");

        try {
            File uploadDirFile = new File(this.uploadDir);
            if (!uploadDirFile.exists()) uploadDirFile.mkdirs();

            String safeFilename = System.currentTimeMillis() + "_" +
                    file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

            File dest = new File(uploadDirFile, safeFilename);
            file.transferTo(dest); // Save file to persistent Railway volume

            ChatAttachment attachment = new ChatAttachment();
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileType(file.getContentType());
            attachment.setFileUrl("/api/chat/download/" + safeFilename);
            attachment.setReceiverId(receiverId);
            if (senderId != null) attachment.setSenderId(senderId);
            attachment.setTimestamp(LocalDateTime.now());

            chatAttachmentService.saveAttachment(attachment); // Persist record

            return ResponseEntity.ok(attachment);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save file");
        }
    }





    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try {
            if (!fileName.matches("[a-zA-Z0-9._-]+")) {
                return ResponseEntity.badRequest().body("Invalid file name");
            }

            File file = new File(this.uploadDir, fileName);
            if (!file.exists()) return ResponseEntity.notFound().build();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(file)); // Serve file for download
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not download file");
        }
    }

}
