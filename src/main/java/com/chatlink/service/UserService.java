package com.chatlink.service;

import com.chatlink.model.User;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public interface UserService {

    User registerUser(User user);

    User login(String email, String password);

    User getUserByUniqueCode(String code);

    User findByEmail(String email);

    boolean verifyUser(String token); // Verify user email using token

    void createPasswordResetToken(String email); // Generate password reset token + send email

    boolean resetPassword(String token, String newPassword); // Reset password using token

    BufferedImage generateQRCode(String text, int width, int height) throws Exception; // Create QR code

    String decodeQRCode(InputStream inputStream) throws Exception; // Decode QR code from image
}
