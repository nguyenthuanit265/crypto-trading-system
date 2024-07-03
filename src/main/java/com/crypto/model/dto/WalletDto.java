package com.crypto.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletDto {
    private String currency;
    private BigDecimal balance;
}
