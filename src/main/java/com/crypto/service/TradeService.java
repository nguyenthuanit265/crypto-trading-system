package com.crypto.service;

import com.crypto.model.dto.TradeRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface TradeService {
    ResponseEntity<?> getTradingHistoryByUserId(HttpServletRequest request, Long userId);

    ResponseEntity<?> executeTrade(HttpServletRequest request, TradeRequest tradeRequest);
}
