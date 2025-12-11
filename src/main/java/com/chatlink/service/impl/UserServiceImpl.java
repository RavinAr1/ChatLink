package com.chatlink.service.impl;

import com.chatlink.model.User;
import com.chatlink.repository.UserRepository;
import com.chatlink.service.UserService;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;


    @Value("${app.base-url}") // Base URL for email links
    private String baseUrl;

    @Value("${spring.mail.from}")
    private String fromEmail;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) throw new RuntimeException("Email already registered");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUniqueCode(java.util.UUID.randomUUID().toString());
        user.setVerified(false);
        user.setVerificationToken(java.util.UUID.randomUUID().toString());

        User savedUser = userRepository.save(user);

        sendVerificationEmail(savedUser); // Send verification email
        return savedUser;
    }

    @Override
    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) throw new RuntimeException("Incorrect password");
        if (!user.isVerified()) throw new RuntimeException("Email not verified. Please check your email.");
        return user;
    }

    @Override
    public User getUserByUniqueCode(String code) {
        return userRepository.findByUniqueCode(code); // Fetch user by unique code
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email); // Fetch user by email
    }

    @Override
    public boolean verifyUser(String token) {
        User user = userRepository.findByVerificationToken(token);
        if (user == null) return false;
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user); // Update verified status
        return true;
    }

    @Override
    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("Email not found");

        String token = java.util.UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(System.currentTimeMillis() + 15 * 60 * 1000); // 15 mins
        userRepository.save(user); // Persist reset token

        sendResetEmail(user); // Send reset email
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token);
        if (user == null || user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry() < System.currentTimeMillis()) return false;

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user); // Update password
        return true;
    }



    // ----------------------------
    // Email sending methods
    // ----------------------------


    private void sendVerificationEmail(User user) {
        String link = baseUrl + "/verify?token=" + user.getVerificationToken();
        String body = "Hi " + user.getFullName() + ",\n\n" +
                "Please verify your email by clicking the link below:\n" +
                link + "\n\nThanks,\nChatLink Team";

        sendEmail(user.getEmail(), "ChatLink - Verify Your Email", body);
    }

    private void sendResetEmail(User user) {
        String link = baseUrl + "/reset-password?token=" + user.getResetToken();
        String body = "Hi " + user.getFullName() + ",\n\n" +
                "Reset your password using the link below:\n" +
                link + "\n\nThis link is valid for 15 minutes.\n\nChatLink Team";

        sendEmail(user.getEmail(), "ChatLink - Reset Password", body);
    }



    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(fromEmail);
        mailSender.send(message);
    }




    @Override
    public BufferedImage generateQRCode(String text, int width, int height) throws Exception {
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix matrix = qrWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF); // Set QR code pixel
            }
        }
        return image;
    }

    @Override
    public String decodeQRCode(InputStream inputStream) throws Exception {
        BufferedImage image = ImageIO.read(inputStream);
        if (image == null) return null;

        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText(); // Extract QR code text
        } catch (NotFoundException e) {
            return null; // Not a valid QR code
        }
    }
}
