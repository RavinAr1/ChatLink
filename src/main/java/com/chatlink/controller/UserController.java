package com.chatlink.controller;

import com.chatlink.model.User;
import com.chatlink.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user); // Save user and send verification email
            model.addAttribute("success", "Registration Successful! Check your email to verify.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "register";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        try {
            User user = userService.login(email, password); // Authenticate user
            session.setAttribute("loggedUser", user);
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login"; // Redirect if not logged in
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // End session
        return "redirect:/login?logout=true";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, Model model) {
        boolean success = userService.verifyUser(token); // Verify email token
        if (success) model.addAttribute("success", "Email verified! You can now login.");
        else model.addAttribute("error", "Invalid or expired verification link.");
        return "login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String sendResetLink(@RequestParam String email, Model model) {
        try {
            userService.createPasswordResetToken(email); // Generate reset token & email
            model.addAttribute("success", "Password reset link sent to your email");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String newPassword,
                                Model model) {
        boolean success = userService.resetPassword(token, newPassword); // Reset user password
        if (success) {
            model.addAttribute("success", "Password reset successful! Login now.");
            return "login";
        } else {
            model.addAttribute("error", "Invalid or expired token.");
            model.addAttribute("token", token);
            return "reset-password";
        }
    }

    @GetMapping(value = "/user/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getUserQRCode(HttpSession session) throws Exception {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) throw new RuntimeException("Not logged in");

        BufferedImage qr = userService.generateQRCode(user.getUniqueCode(), 200, 200);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qr, "png", baos);
        return baos.toByteArray(); // Return QR code image
    }
}
