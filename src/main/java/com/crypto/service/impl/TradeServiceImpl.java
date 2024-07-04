package com.crypto.service.impl;

import com.crypto.model.dto.TradeRequest;
import com.crypto.model.entity.Price;
import com.crypto.model.entity.Trade;
import com.crypto.model.entity.User;
import com.crypto.model.entity.Wallet;
import com.crypto.model.response.AppResponse;
import com.crypto.repository.PriceRepository;
import com.crypto.repository.TradeRepository;
import com.crypto.repository.UserRepository;
import com.crypto.repository.WalletRepository;
import com.crypto.service.PriceAggregationService;
import com.crypto.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final PriceRepository priceRepository;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResponseEntity<?> executeTrade(HttpServletRequest request, TradeRequest tradeRequest) {
        AppResponse appResponse = new AppResponse();
        appResponse.setPath(request.getServletPath());

        User user = userRepository.findById(tradeRequest.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        Wallet userUsdtWallet = walletRepository.findByUserIdAndCurrency(tradeRequest.getUserId(), "USDT");
        Wallet userCryptoWallet = walletRepository.findByUserIdAndCurrency(tradeRequest.getUserId(), getCurrencyFromTradingPair(tradeRequest.getTradingPair()));

        if (userUsdtWallet == null) {
            appResponse.setData(null);
            appResponse.setError(new RuntimeException("User USDT wallet not found.").getMessage());
            appResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            appResponse.setMessage("User USDT wallet not found.");
            return new ResponseEntity<>(appResponse, HttpStatus.BAD_REQUEST);
        }

        if (userCryptoWallet == null) {
            userCryptoWallet = new Wallet();
            userCryptoWallet.setUser(user);
        }

        Price latestPrice = priceRepository.findTopByOrderByCreatedAtDesc();

        if (latestPrice == null) {
            appResponse.setData(null);
            appResponse.setError(new RuntimeException("No price data available").getMessage());
            appResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            appResponse.setMessage("No price data available");
            return new ResponseEntity<>(appResponse, HttpStatus.BAD_REQUEST);
        }
        double price = tradeRequest.getType().equalsIgnoreCase("BUY") ? latestPrice.getAskPrice().doubleValue() : latestPrice.getBidPrice().doubleValue();
        BigDecimal tradePrice;
        if ("BUY".equalsIgnoreCase(tradeRequest.getType())) {
            tradePrice = latestPrice.getAskPrice();
        } else if ("SELL".equalsIgnoreCase(tradeRequest.getType())) {
            tradePrice = latestPrice.getBidPrice();
        } else {
            appResponse.setData(null);
            appResponse.setError(new RuntimeException("Invalid trade type.").getMessage());
            appResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            appResponse.setMessage("Invalid trade type.");
            return new ResponseEntity<>(appResponse, HttpStatus.BAD_REQUEST);
        }

        BigDecimal cost = tradePrice.multiply(tradeRequest.getAmount());
        if ("BUY".equalsIgnoreCase(tradeRequest.getType())) {
            if (userUsdtWallet.getBalance().compareTo(cost) < 0) {
                appResponse.setData(null);
                appResponse.setError(new RuntimeException("Insufficient USDT balance.").getMessage());
                appResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                appResponse.setMessage("Insufficient USDT balance.");
                return new ResponseEntity<>(appResponse, HttpStatus.BAD_REQUEST);
            }
            userUsdtWallet.setBalance(userUsdtWallet.getBalance().subtract(cost));

            userCryptoWallet.setCurrency(getCurrencyFromTradingPair(tradeRequest.getTradingPair()));
            if (userCryptoWallet.getBalance() == null) {
                userCryptoWallet.setBalance(tradeRequest.getAmount());
            } else {
                userCryptoWallet.setBalance(userCryptoWallet.getBalance().add(tradeRequest.getAmount()));
            }
        } else if ("SELL".equalsIgnoreCase(tradeRequest.getType())) {
            if (userCryptoWallet.getBalance() == null || userCryptoWallet.getBalance().compareTo(tradeRequest.getAmount()) < 0) {
                appResponse.setData(null);
                appResponse.setError(new RuntimeException("Insufficient cryptocurrency balance.").getMessage());
                appResponse.setStatus(HttpStatus.BAD_REQUEST.value());
                appResponse.setMessage("Insufficient cryptocurrency balance.");
                return new ResponseEntity<>(appResponse, HttpStatus.BAD_REQUEST);
            }
            userCryptoWallet.setBalance(userCryptoWallet.getBalance().subtract(tradeRequest.getAmount()));
            userUsdtWallet.setBalance(userUsdtWallet.getBalance().add(cost));
        }

        Trade transaction = new Trade();
        transaction.setUser(user);
        transaction.setTradingPair(tradeRequest.getTradingPair());
        transaction.setOrderType(tradeRequest.getType());
        transaction.setAmount(tradeRequest.getAmount());
        transaction.setPrice(BigDecimal.valueOf(price));
        transaction.setCreatedAt(ZonedDateTime.now());

        walletRepository.save(userUsdtWallet);
        walletRepository.save(userCryptoWallet);
        tradeRepository.save(transaction);

        appResponse.setMessage("Trade executed successfully");
        appResponse.setData(tradeRequest);
        appResponse.setStatus(HttpStatus.OK.value());
        return new ResponseEntity<>(appResponse, HttpStatus.OK);
    }

    private String getCurrencyFromTradingPair(String tradingPair) {
        if (tradingPair.equalsIgnoreCase("ETHUSDT")) {
            return "ETH";
        } else if (tradingPair.equalsIgnoreCase("BTCUSDT")) {
            return "BTC";
        } else {
            throw new RuntimeException("Invalid trading pair.");
        }
    }
}
