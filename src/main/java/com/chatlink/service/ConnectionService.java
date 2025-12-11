package com.chatlink.service;

import com.chatlink.model.ConnectionRequest;
import com.chatlink.model.User;
import java.util.List;

public interface ConnectionService {

    ConnectionRequest sendRequest(Long senderId, Long receiverId);

    List<ConnectionRequest> getPendingRequests(Long receiverId);

    void acceptRequest(Long requestId);

    void rejectRequest(Long requestId);

    List<User> getUserConnections(Long userId); // Get all accepted connections of a user
}
