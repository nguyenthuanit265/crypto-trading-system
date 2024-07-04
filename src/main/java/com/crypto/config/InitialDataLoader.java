package com.crypto.config;

import com.crypto.model.entity.User;
import com.crypto.model.entity.Wallet;
import com.crypto.repository.UserRepository;
import com.crypto.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class InitialDataLoader {

    // Assumption User's initial wallet balance 50,000 USDT in DB record.
    @Bean
    CommandLineRunner initializeWallets(UserRepository userRepository, WalletRepository walletRepository) {
        return args -> {
            List<User> users = userRepository.findAll();
            if (CollectionUtils.isEmpty(users)) {
                User newUser = new User();
                newUser.setEmail("user1@gmail.com");
                newUser.setUsername("user1");
                userRepository.save(newUser);
                users.add(newUser);
            }
            for (User user : users) {
                Wallet existingWallet = walletRepository.findByUserIdAndCurrency(user.getId(), "USDT");
                if (existingWallet == null) {
                    Wallet initialWallet = new Wallet();
                    initialWallet.setUser(user);
                    initialWallet.setCurrency("USDT");
                    initialWallet.setBalance(new BigDecimal("50000.00"));
                    walletRepository.save(initialWallet);
                }
            }
        };
    }
}
