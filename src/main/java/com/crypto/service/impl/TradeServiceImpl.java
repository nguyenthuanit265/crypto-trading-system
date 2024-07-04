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
        User user = userRepository.findById(tradeRequest.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        Wallet userUsdtWallet = walletRepository.findByUserIdAndCurrency(tradeRequest.getUserId(), "USDT");
        Wallet userCryptoWallet = walletRepository.findByUserIdAndCurrency(tradeRequest.getUserId(), getCurrencyFromTradingPair(tradeRequest.getTradingPair()));
        if (userUsdtWallet == null) {
            throw new RuntimeException("User USDT wallet not found.");
        }

//        if (userCryptoWallet == null) {
//            throw new RuntimeException("User cryptocurrency wallet not found.");
//        }

        Price latestPrice = priceRepository.findTopByOrderByCreatedAtDesc();

        if (latestPrice == null) {
            return ResponseEntity.badRequest().body("No price data available");
        }
        double price = tradeRequest.getType().equalsIgnoreCase("BUY") ? latestPrice.getAskPrice().doubleValue() : latestPrice.getBidPrice().doubleValue();
        BigDecimal tradePrice;
        if ("BUY".equalsIgnoreCase(tradeRequest.getType())) {
            tradePrice = latestPrice.getAskPrice();
        } else if ("SELL".equalsIgnoreCase(tradeRequest.getType())) {
            tradePrice = latestPrice.getBidPrice();
        } else {
            throw new RuntimeException("Invalid trade type.");
        }

        BigDecimal cost = tradePrice.multiply(tradeRequest.getAmount());
        if ("BUY".equalsIgnoreCase(tradeRequest.getType())) {
            if (userUsdtWallet.getBalance().compareTo(cost) < 0) {
                throw new RuntimeException("Insufficient USDT balance.");
            }
            userUsdtWallet.setBalance(userUsdtWallet.getBalance().subtract(cost));
            userCryptoWallet.setBalance(userCryptoWallet.getBalance().add(tradeRequest.getAmount()));
        } else if ("SELL".equalsIgnoreCase(tradeRequest.getType())) {
            if (userCryptoWallet.getBalance().compareTo(tradeRequest.getAmount()) < 0) {
                throw new RuntimeException("Insufficient cryptocurrency balance.");
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

        return ResponseEntity.ok("Trade executed successfully");
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
