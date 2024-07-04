package com.crypto.service.impl;

import com.crypto.model.dto.TradeRequest;
import com.crypto.model.entity.Trade;
import com.crypto.model.response.AppResponse;
import com.crypto.repository.TradeRepository;
import com.crypto.repository.WalletRepository;
import com.crypto.service.PriceAggregationService;
import com.crypto.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final WalletRepository walletRepository;
    private final PriceAggregationService priceAggregationService;

    @Override
    public ResponseEntity<?> getTradingHistoryByUserId(HttpServletRequest request, Long userId) {
        AppResponse response = new AppResponse();
        try {
            List<Trade> trades = tradeRepository.findByUserId(userId);
            response.setPath(request.getServletPath());
            response.setStatus(HttpStatus.OK.value());
            response.setData(trades);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("TradeServiceImpl > getTradingHistoryByUserId - error getTradingHistoryByUserId, error = {}", ex.getMessage(), ex);
            response.setError(ex.getMessage());
            response.setPath(request.getServletPath());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setData(Collections.emptyList());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> executeTrade(HttpServletRequest request, TradeRequest tradeRequest) {
        return null;
    }
}
