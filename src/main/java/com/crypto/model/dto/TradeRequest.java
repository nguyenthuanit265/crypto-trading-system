package com.crypto.model.dto;

import com.crypto.annotation.HashText;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TradeRequest {

    @HashText(fieldName = "userId")
    @JsonProperty("userId")
    private Long userId;

    @HashText(fieldName = "tradingPair", acceptValues = {"ETHUSDT", "BTCUSDT"})
    @JsonProperty("tradingPair")
    private String tradingPair; // "ETHUSDT" or "BTCUSDT"

    @HashText(fieldName = "type", acceptValues = {"BUY", "SELL"})
    @JsonProperty("type")
    private String type; // "BUY" or "SELL"

    @HashText(fieldName = "amount")
    @JsonProperty("amount")
    private BigDecimal amount;
}
