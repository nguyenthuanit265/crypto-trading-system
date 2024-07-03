package com.crypto.schedules;

import com.crypto.service.PriceAggregationService;
import com.crypto.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PriceAggregationJob {
    private final PriceAggregationService priceAggregationService;

    @Scheduled(fixedRate = 10000)
    public void aggregatePrices() {
        log.info("PriceAggregationService > aggregatePrices - START");
        priceAggregationService.fetchPrices();
        log.info("PriceAggregationService > aggregatePrices - END");
    }
}
