package com.crypto.controller;

import com.crypto.model.response.AppResponse;
import com.crypto.service.PriceAggregationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/price-aggregations")
public class PriceAggregationController {
    private final PriceAggregationService priceAggregationService;

    @GetMapping("/run")
    public ResponseEntity<?> priceAggregation(HttpServletRequest request) {
        log.info("PriceAggregationController > priceAggregation - start::rest");
        priceAggregationService.fetchPrices();
        AppResponse appResponse = AppResponse.buildResponse("", request.getRequestURI(), "Price aggregation successfully", HttpStatus.OK.value(), null);
        return new ResponseEntity<>(appResponse, HttpStatus.OK);
    }
}
