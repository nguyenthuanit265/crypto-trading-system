package com.crypto.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface PriceService {
    ResponseEntity<?> getLatestPrice(HttpServletRequest request);
}
