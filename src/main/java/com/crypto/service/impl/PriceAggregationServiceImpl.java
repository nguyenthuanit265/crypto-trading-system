package com.crypto.service.impl;

import com.crypto.model.dto.BinanceTicker;
import com.crypto.model.dto.HuobiResponse;
import com.crypto.model.dto.HuobiTicker;
import com.crypto.model.entity.Price;
import com.crypto.repository.PriceRepository;
import com.crypto.service.PriceAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PriceAggregationServiceImpl implements PriceAggregationService {
    @Value("${external.binance.url}")
    private String binanceUrl;

    @Value("${external.huobi.url}")
    private String huobiUrl;

    private final RestTemplate restTemplate;
    private final PriceRepository priceRepository;

    @Override
    public void fetchPrices() {
        CompletableFuture<BinanceTicker[]> binanceFuture = CompletableFuture.supplyAsync(() ->
                restTemplate.getForObject(binanceUrl, BinanceTicker[].class));
        CompletableFuture<HuobiResponse> huobiFuture = CompletableFuture.supplyAsync(() ->
                restTemplate.getForObject(huobiUrl, HuobiResponse.class));
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(binanceFuture, huobiFuture);

        try {
            allFutures.get();  // Wait for both futures to complete
            BinanceTicker[] binancePrices = binanceFuture.get();
            HuobiResponse huobiResponse = huobiFuture.get();

            Optional<BinanceTicker> binanceEthUsdt = findTicker(binancePrices, "ETHUSDT");
            Optional<BinanceTicker> binanceBtcUsdt = findTicker(binancePrices, "BTCUSDT");
            Optional<HuobiTicker> huobiEthUsdt = findTicker(huobiResponse.getData(), "ethusdt");
            Optional<HuobiTicker> huobiBtcUsdt = findTicker(huobiResponse.getData(), "btcusdt");

            // Aggregate prices and store the best price
            BigDecimal bestEthBidPrice = aggregateBestBid(binanceEthUsdt, huobiEthUsdt);
            BigDecimal bestEthAskPrice = aggregateBestAsk(binanceEthUsdt, huobiEthUsdt);
            BigDecimal bestBtcBidPrice = aggregateBestBid(binanceBtcUsdt, huobiBtcUsdt);
            BigDecimal bestBtcAskPrice = aggregateBestAsk(binanceBtcUsdt, huobiBtcUsdt);

            Price ethPrice = new Price("ETHUSDT", bestEthBidPrice, bestEthAskPrice);
            Price btcPrice = new Price("BTCUSDT", bestBtcBidPrice, bestBtcAskPrice);

            priceRepository.saveAll(List.of(ethPrice, btcPrice));
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();  // Handle exceptions
        }
    }

    private Optional<BinanceTicker> findTicker(BinanceTicker[] tickers, String symbol) {
        return Arrays.stream(tickers)
                .filter(ticker -> ticker.getSymbol().equalsIgnoreCase(symbol))
                .findFirst();
    }

    private Optional<HuobiTicker> findTicker(List<HuobiTicker> tickers, String symbol) {
        return tickers.stream()
                .filter(ticker -> ticker.getSymbol().equalsIgnoreCase(symbol))
                .findFirst();
    }

    private BigDecimal aggregateBestBid(Optional<BinanceTicker> binanceTicker, Optional<HuobiTicker> huobiTicker) {
        BigDecimal binanceBid = binanceTicker.map(t -> new BigDecimal(t.getBidPrice())).orElse(BigDecimal.ZERO);
        BigDecimal huobiBid = huobiTicker.map(HuobiTicker::getBid).orElse(BigDecimal.ZERO);

        return binanceBid.max(huobiBid);
    }

    private BigDecimal aggregateBestAsk(Optional<BinanceTicker> binanceTicker, Optional<HuobiTicker> huobiTicker) {
        BigDecimal binanceAsk = binanceTicker.map(t -> new BigDecimal(t.getAskPrice())).orElse(BigDecimal.ZERO);
        BigDecimal huobiAsk = huobiTicker.map(HuobiTicker::getAsk).orElse(BigDecimal.ZERO);

        return binanceAsk.min(huobiAsk);
    }
}
