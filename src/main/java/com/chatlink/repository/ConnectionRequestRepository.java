package com.chatlink.repository;

import com.chatlink.model.ConnectionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    List<ConnectionRequest> findByReceiverIdAndStatus(Long receiverId, String status); // Pending requests received by user

    List<ConnectionRequest> findBySenderId(Long senderId); // Requests sent by user

    List<ConnectionRequest> findBySenderIdAndStatusOrReceiverIdAndStatus(
            Long senderId, String status1,
            Long receiverId, String status2
    ); // Requests either sent or received by user with given statuses
}
