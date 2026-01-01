package com.chatlink.service.impl;

import com.chatlink.model.User;
import com.chatlink.repository.UserRepository;
import com.chatlink.service.UserService;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;

import javax.imageio.ImageIO;

import com.google.zxing.*;

import com.google.zxing.Result;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url}") // Base URL for email links
    private String baseUrl;

    @Value("${spring.mail.from}") // From email address
    private String fromEmail;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User registerUser(User user) {   // Registration with email verification
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUniqueCode(java.util.UUID.randomUUID().toString()); // Unique code generation

        // Email verification token
        user.setVerified(false);
        user.setVerificationToken(java.util.UUID.randomUUID().toString());  // Generate token

        User savedUser = userRepository.save(user);

        // Send verification email
        sendVerificationEmail(savedUser);

        return savedUser;
    }

    @Override
    public User login(String email, String rawPassword) {       // Login with email verification check
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {        // Check password
            throw new RuntimeException("Incorrect password");
        }
        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified. Please check your email.");
        }
        return user;
    }

    @Override
    public User getUserByUniqueCode(String code) {      // Find user by unique code
        return userRepository.findByUniqueCode(code);
    }

    @Override
    public User findByEmail(String email) {     // Find user by email
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean verifyUser(String token) {      // Email verification
        User user = userRepository.findByVerificationToken(token);
        if (user == null) return false; // Invalid token
        user.setVerified(true);
        user.setVerificationToken(null);        // Clear token after verification
        userRepository.save(user);
        return true;
    }

    @Override
    public void createPasswordResetToken(String email) {        // Password reset token generation and email sending
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("Email not found");

        String token = java.util.UUID.randomUUID().toString();      // Generate reset token
        user.setResetToken(token);      // Set token
        user.setResetTokenExpiry(System.currentTimeMillis() + 15 * 60 * 1000); // 15 mins expiry
        userRepository.save(user);

        sendResetEmail(user);
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {    // Password reset using token
        User user = userRepository.findByResetToken(token);     // Find user by reset token
        if (user == null || user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry() < System.currentTimeMillis()) {      // Check token validity
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));      // Update password
        user.setResetToken(null);       // Clear reset token
        user.setResetTokenExpiry(null);     // Clear reset token and expiry
        userRepository.save(user);
        return true;
    }

    // ----------------------------
    // Real email sending methods
    // ----------------------------
    private void sendVerificationEmail(User user) {         // Send verification email
        String link = baseUrl + "/verify?token=" + user.getVerificationToken();
        String body = "Hi " + user.getFullName() + ",\n\n"
                + "Please verify your email by clicking the link below:\n"
                + link + "\n\nThanks,\nChatLink Team";

        sendEmail(user.getEmail(), "ChatLink - Verify Your Email", body);
    }

    private void sendResetEmail(User user) {        // Send password reset email
        String link = baseUrl + "/reset-password?token=" + user.getResetToken();
        String body = "Hi " + user.getFullName() + ",\n\n"
                + "Reset your password using the link below:\n"
                + link + "\n\nThis link is valid for 15 minutes.\n\nChatLink Team";

        sendEmail(user.getEmail(), "ChatLink - Reset Password", body);
    }

    private void sendEmail(String toEmail, String subject, String body) {       // General email sending method
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // use fromEmail from properties
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public BufferedImage generateQRCode(String text, int width, int height) throws Exception {      // QR code generation
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix matrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, width, height);     // Create BitMatrix

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);     // Create image

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        return image;
    }

    @Override
    public String decodeQRCode(InputStream inputStream) throws Exception {      // QR code decoding
        BufferedImage image = ImageIO.read(inputStream);        // Read image from input stream
        if (image == null) return null;

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result result;

        try {
            result = new MultiFormatReader().decode(bitmap);    // Decode the QR code
            return result.getText();
        } catch (NotFoundException e) {
            return null; // Not a valid QR code
        }
    }
}
