package com.ecommerce.user;

import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@shophub.com", passwordEncoder.encode("admin123"), "Admin User");
                admin.setRole(User.Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Admin user seeded: admin / admin123");
            }
        };
    }
}
