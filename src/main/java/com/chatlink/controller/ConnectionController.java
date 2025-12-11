package com.chatlink.controller;

import com.chatlink.model.ConnectionRequest;
import com.chatlink.model.User;
import com.chatlink.service.ConnectionService;
import com.chatlink.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ConnectionController {

    private final UserService userService;
    private final ConnectionService connectionService;

    @GetMapping("/connect")
    public String showConnectPage() {
        return "connect";
    }

    @PostMapping("/connect")
    public String sendRequest(@RequestParam String code, HttpSession session, Model model) {

        User sender = (User) session.getAttribute("loggedUser");
        User receiver = userService.getUserByUniqueCode(code);

        if (receiver == null) {
            model.addAttribute("error", "Invalid Code!");
            return "connect";
        }

        connectionService.sendRequest(sender.getId(), receiver.getId()); // Send connection request
        model.addAttribute("success", "Request Sent!");

        return "connect";
    }

    @GetMapping("/requests")
    public String viewRequests(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");

        List<ConnectionRequest> requests = connectionService.getPendingRequests(user.getId());
        model.addAttribute("requests", requests); // Show pending requests

        return "requests";
    }

    @PostMapping("/requests/accept")
    public String accept(@RequestParam Long requestId) {
        connectionService.acceptRequest(requestId); // Accept connection request
        return "redirect:/requests";
    }

    @PostMapping("/requests/reject")
    public String reject(@RequestParam Long requestId) {
        connectionService.rejectRequest(requestId); // Reject connection request
        return "redirect:/requests";
    }
}
