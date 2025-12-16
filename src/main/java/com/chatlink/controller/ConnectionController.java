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

    @GetMapping("/connect")     // Show connections page
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

        ConnectionRequest request = connectionService.sendRequest(sender.getId(), receiver.getId());

        if (request != null) {
            model.addAttribute("success", "Connection request sent successfully!");
        } else {
            model.addAttribute("error", "You have already sent a request to this user.");
        }

        return "connect";
    }



    @GetMapping("/requests")       // View received connection requests
    public String viewRequests(HttpSession session, Model model) {

        User user = (User) session.getAttribute("loggedUser");

        List<ConnectionRequest> requests = connectionService.getPendingRequests(user.getId());      // Fetch pending requests
        model.addAttribute("requests", requests);

        return "requests";
    }

    @PostMapping("/requests/accept")    // Accept connection request
    public String accept(@RequestParam Long requestId) {
        connectionService.acceptRequest(requestId);
        return "redirect:/requests";
    }

    @PostMapping("/requests/reject")    // Reject connection request
    public String reject(@RequestParam Long requestId) {
        connectionService.rejectRequest(requestId);
        return "redirect:/requests";
    }
}
