package com.crypto.service.impl;

import com.crypto.model.dto.WalletDto;
import com.crypto.model.entity.Wallet;
import com.crypto.model.response.AppResponse;
import com.crypto.repository.WalletRepository;
import com.crypto.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public ResponseEntity<?> getUserWalletBalance(Long userId) {
        AppResponse appResponse = new AppResponse();
        List<Wallet> wallets = walletRepository.findByUserId(userId);
        if (CollectionUtils.isEmpty(wallets)) {
            appResponse.setData(Collections.emptyList());
            appResponse.setStatus(HttpStatus.NO_CONTENT.value());
            return new ResponseEntity<>(appResponse, HttpStatus.OK);
        }

        List<WalletDto> walletData = wallets.stream().map(wallet -> {
            WalletDto dto = new WalletDto();
            dto.setCurrency(wallet.getCurrency());
            dto.setBalance(wallet.getBalance());
            return dto;
        }).toList();
        return new ResponseEntity<>(walletData, HttpStatus.OK);
    }
}
