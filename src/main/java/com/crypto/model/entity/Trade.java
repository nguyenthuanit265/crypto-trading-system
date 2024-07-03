package com.crypto.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(name = "`trade`")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String tradingPair;
    private String orderType;
    private BigDecimal price;
    private BigDecimal amount;
    private ZonedDateTime createdAt;

    public Trade() {
        this.createdAt = ZonedDateTime.now();
    }

}
