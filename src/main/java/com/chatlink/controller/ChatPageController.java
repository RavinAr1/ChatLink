package com.chatlink.controller;

import com.chatlink.model.User;
import com.chatlink.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatPageController {

    private final ConnectionService connectionService; // Service to fetch user connections

    @GetMapping("/chat")    // Mapping for chat page
    public String openChatPage(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<User> connections = connectionService.getUserConnections(user.getId());    // Fetch user connections

        model.addAttribute("user", user);
        model.addAttribute("connections", connections);

        return "chat"; // Return chat page view
    }
}
