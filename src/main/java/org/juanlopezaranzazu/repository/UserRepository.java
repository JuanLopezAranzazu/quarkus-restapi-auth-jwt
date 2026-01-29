package org.juanlopezaranzazu.repository;

import java.util.Optional;

import org.juanlopezaranzazu.entity.User;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class UserRepository implements PanacheMongoRepository<User> {
    public Optional<User> findByUsername(String username) {
        return find("username", username).firstResultOptional();
    }

    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<User> findByRefreshToken(String refreshToken) {
        return find("refreshToken", refreshToken).firstResultOptional();
    }

    public boolean existsByUsername(String username) {
        return count("username", username) > 0;
    }

    public boolean existsByEmail(String email) {
        return count("email", email) > 0;
    }
    
}
