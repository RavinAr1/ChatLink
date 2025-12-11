package com.chatlink.service.impl;

import com.chatlink.model.ConnectionRequest;
import com.chatlink.model.User;
import com.chatlink.repository.ConnectionRequestRepository;
import com.chatlink.repository.UserRepository;
import com.chatlink.service.ConnectionService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final ConnectionRequestRepository requestRepo;
    private final UserRepository userRepo;

    @Override
    public ConnectionRequest sendRequest(Long senderId, Long receiverId) {
        if (!userRepo.existsById(receiverId)) throw new RuntimeException("User does not exist!");

        ConnectionRequest req = new ConnectionRequest();
        req.setSenderId(senderId);
        req.setReceiverId(receiverId);
        req.setStatus("PENDING");

        return requestRepo.save(req); // Persist connection request
    }

    @Override
    public List<ConnectionRequest> getPendingRequests(Long receiverId) {
        return requestRepo.findByReceiverIdAndStatus(receiverId, "PENDING"); // Fetch pending requests
    }

    @Override
    public void acceptRequest(Long requestId) {
        ConnectionRequest req = requestRepo.findById(requestId).orElseThrow();
        req.setStatus("ACCEPTED");
        requestRepo.save(req); // Update status to ACCEPTED
    }

    @Override
    public void rejectRequest(Long requestId) {
        ConnectionRequest req = requestRepo.findById(requestId).orElseThrow();
        req.setStatus("REJECTED");
        requestRepo.save(req); // Update status to REJECTED
    }

    @Override
    public List<User> getUserConnections(Long userId) {
        List<ConnectionRequest> requests = requestRepo.findBySenderIdAndStatusOrReceiverIdAndStatus(
                userId, "ACCEPTED",
                userId, "ACCEPTED"
        );

        return requests.stream()
                .map(req -> req.getSenderId().equals(userId) ?
                        userRepo.findById(req.getReceiverId()).orElse(null) :
                        userRepo.findById(req.getSenderId()).orElse(null))
                .filter(u -> u != null)
                .distinct() // Remove duplicates
                .collect(Collectors.toList()); // Return list of connected users
    }

    public BufferedImage generateQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            return MatrixToImageWriter.toBufferedImage(bitMatrix); // Generate QR code image
        } catch (WriterException e) {
            throw new RuntimeException("Could not generate QR Code", e);
        }
    }
}
