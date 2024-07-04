package com.crypto.controller;

import com.crypto.model.dto.TradeRequest;
import com.crypto.model.response.AppResponse;
import com.crypto.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/trade")
    public ResponseEntity<?> executeTrade(HttpServletRequest request, @RequestBody @Valid TradeRequest tradeRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(AppResponse.buildResponse(
                    Objects.requireNonNull(bindingResult.getFieldError()).getField(),
                    request.getServletPath(),
                    Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage(),
                    HttpStatus.BAD_REQUEST.value(), null
            )
                    , HttpStatus.BAD_REQUEST);
        }
        return tradeService.executeTrade(request, tradeRequest);
    }

    @GetMapping("/trades/{userId}")
    public ResponseEntity<?> getTradingHistory(HttpServletRequest request, @PathVariable Long userId) {
        return tradeService.getTradingHistoryByUserId(request, userId);
    }
}
