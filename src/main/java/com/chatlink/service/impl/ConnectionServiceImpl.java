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
        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepo.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // Check if request already exists
        boolean alreadySent = requestRepo.findAll().stream()
                .anyMatch(req ->
                        req.getSender().getId().equals(senderId) &&
                                req.getReceiver().getId().equals(receiverId) &&
                                "PENDING".equals(req.getStatus())
                );

        if (alreadySent) return null;

        ConnectionRequest req = new ConnectionRequest();
        req.setSender(sender);
        req.setReceiver(receiver);
        req.setStatus("PENDING");

        return requestRepo.save(req);
    }


    @Override
    /* Fetch pending connection requests for a user */
    public List<ConnectionRequest> getPendingRequests(Long userId) {
        // Fetch requests where receiver is user and status is PENDING
        List<ConnectionRequest> requests = requestRepo.findByReceiverIdAndStatus(userId, "PENDING");
        return requests;
    }


    @Override
    /* Accept a connection request */
    public void acceptRequest(Long requestId) {
        ConnectionRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found!"));
        req.setStatus("ACCEPTED");
        requestRepo.save(req);
    }

    @Override
    /* Reject a connection request */
    public void rejectRequest(Long requestId) {
        ConnectionRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found!"));
        req.setStatus("REJECTED");
        requestRepo.save(req);
    }



    @Override
    /* Get all accepted connections for a user */
    public List<User> getUserConnections(Long userId) {
        List<ConnectionRequest> requests = requestRepo.findBySenderIdAndStatusOrReceiverIdAndStatus(
                userId, "ACCEPTED",
                userId, "ACCEPTED"
        );

        return requests.stream()
                .map(req -> req.getSender().getId().equals(userId) ?
                        req.getReceiver() : req.getSender())
                .filter(u -> u != null)
                .distinct()
                .collect(Collectors.toList());
    }



/*
    Generate a QR code image from the given text
 */
    public BufferedImage generateQRCode(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            throw new RuntimeException("Could not generate QR Code", e);
        }
    }
}
