package com.crypto.service;

import org.springframework.http.ResponseEntity;

public interface WalletService {
    ResponseEntity<?> getUserWalletBalance(Long userId);
}
