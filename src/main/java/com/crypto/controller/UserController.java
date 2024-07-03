package com.crypto.controller;

import com.crypto.model.entity.Trade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {
//    @GetMapping("/trades/{userId}")
//    public ResponseEntity<?> getTradingHistory(@PathVariable Long userId) {
//        List<Trade> trades = tradeRepository.findByUserId(userId);
//        return ResponseEntity.ok(trades);
//    }
}
