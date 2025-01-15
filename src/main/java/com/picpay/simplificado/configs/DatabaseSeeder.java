package com.picpay.simplificado.configs;

import com.picpay.simplificado.domain.user.User;
import com.picpay.simplificado.domain.user.UserType;
import com.picpay.simplificado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Arrays;

@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Autowired
    public DatabaseSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        User user1 = new User(null, "Victor", "Andrade", "12616076942", "victor@gmail.com", "victor123456", BigDecimal.valueOf(200), UserType.COMMON);
        User user2 = new User(null, "Luiza", "Aurora", "12345678912", "luiza@gmail.com", "luiza123456", BigDecimal.valueOf(100), UserType.MERCHANT);
        userRepository.saveAll(Arrays.asList(user1, user2));
    }
}
