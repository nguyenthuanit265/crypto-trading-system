package com.crypto.service.impl;

import com.crypto.model.response.AppResponse;
import com.crypto.repository.PriceRepository;
import com.crypto.service.PriceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;

    @Override
    public ResponseEntity<?> getLatestPrice(HttpServletRequest request) {
        AppResponse response = new AppResponse();
        try {
            response.setPath(request.getServletPath());
            response.setStatus(HttpStatus.OK.value());
            response.setData(priceRepository.findTopByOrderByCreatedAtDesc());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("PriceServiceImpl > getLatestPrice - error getLatestPrice, error = {}", ex.getMessage(), ex);
            response.setError(ex.getMessage());
            response.setPath(request.getServletPath());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setData(Collections.emptyList());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}