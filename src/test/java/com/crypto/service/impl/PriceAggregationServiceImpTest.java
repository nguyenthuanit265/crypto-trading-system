package com.crypto.service.impl;

import com.crypto.service.PriceAggregationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class PriceAggregationServiceImpTest {

    @InjectMocks
    private PriceAggregationServiceImpl priceAggregationService;

    @Test
    public void testPasswordEncoderWorks() {
    }
}
