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

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")     // Foreign key to the User who sent the request
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")   //  Foreign key to the User who received the request
    private User receiver;
}
