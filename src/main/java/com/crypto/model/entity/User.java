package com.crypto.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`user`")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private ZonedDateTime createdAt;

    public User() {
        this.createdAt = ZonedDateTime.now();
    }
}