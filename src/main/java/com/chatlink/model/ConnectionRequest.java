package com.chatlink.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "connection_requests")
public class ConnectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;

    private String status;

    @Transient
    private User sender;

    @Transient
    private User receiver;
}
