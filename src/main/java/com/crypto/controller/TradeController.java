package com.crypto.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/trades")
public class TradeController {
//    @PostMapping("/trades")
//    public ResponseEntity<?> createTrade(@RequestBody @Valid TradeRequest tradeRequest) {
////        Trade trade = tradeService.executeTrade(tradeRequest);
////        return ResponseEntity.ok(trade);
//    }
}
