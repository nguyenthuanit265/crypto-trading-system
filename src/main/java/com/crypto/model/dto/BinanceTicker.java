package com.crypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BinanceTicker {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("bidPrice")
    private String bidPrice;

    @JsonProperty("bidQty")
    private String bidQty;

    @JsonProperty("askPrice")
    private String askPrice;

    @JsonProperty("askQty")
    private String askQty;
}
