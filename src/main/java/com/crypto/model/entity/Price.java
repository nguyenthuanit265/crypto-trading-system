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
@Table(name = "`price`")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String exchange;
    private String symbol;
    private String tradingPair;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private ZonedDateTime createdAt;

    public Price() {
        this.createdAt = ZonedDateTime.now();
    }

    public Price(String tradingPair, BigDecimal bidPrice, BigDecimal askPrice) {
        this.tradingPair = tradingPair;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
        this.createdAt = ZonedDateTime.now();
    }
}
