package com.crypto.controller;

import com.crypto.service.PriceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/prices")
public class PriceController {

    private final PriceService priceService;

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestPrice(HttpServletRequest request) {
        log.info("PriceController > getLatestPrice - start::rest");
        return priceService.getLatestPrice(request);
    }
}
