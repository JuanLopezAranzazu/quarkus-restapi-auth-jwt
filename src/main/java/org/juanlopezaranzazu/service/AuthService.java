package org.juanlopezaranzazu.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.bson.types.ObjectId;
import org.juanlopezaranzazu.dto.CreateUserDTO;
import org.juanlopezaranzazu.dto.LoginDTO;
import org.juanlopezaranzazu.dto.RefreshTokenDTO;
import org.juanlopezaranzazu.dto.TokenDTO;
import org.juanlopezaranzazu.dto.UserDTO;
import org.juanlopezaranzazu.entity.User;
import org.juanlopezaranzazu.exception.BadRequestException;
import org.juanlopezaranzazu.exception.UnauthorizedException;
import org.juanlopezaranzazu.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Arrays;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    JwtService jwtService;

    public UserDTO register(CreateUserDTO createUserDTO) {
        // Validar si el usuario ya existe
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        // Crear nuevo usuario
        User user = new User();
        user.username = createUserDTO.getUsername();
        user.email = createUserDTO.getEmail();
        user.password = BCrypt.hashpw(createUserDTO.getPassword(), BCrypt.gensalt());
        user.firstName = createUserDTO.getFirstName();
        user.lastName = createUserDTO.getLastName();
        user.roles = Arrays.asList("USER");
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();

        userRepository.persist(user);

        return mapToDTO(user);
    }

    public TokenDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!BCrypt.checkpw(loginDTO.getPassword(), user.password)) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Generar tokens
        String accessToken = jwtService.generateAccessToken(
                user.username,
                user.id.toString(),
                user.roles.toArray(new String[0])
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.username,
                user.id.toString()
        );

        // Guardar refresh token en la base de datos
        user.refreshToken = refreshToken;
        user.refreshTokenExpiry = LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenDuration());
        userRepository.update(user);

        return new TokenDTO(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenDuration()
        );
    }

    public TokenDTO refreshToken(RefreshTokenDTO refreshTokenDTO) {
        User user = userRepository.findByRefreshToken(refreshTokenDTO.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (user.refreshTokenExpiry.isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token has expired");
        }

        // Generar nuevo access token
        String newAccessToken = jwtService.generateAccessToken(
                user.username,
                user.id.toString(),
                user.roles.toArray(new String[0])
        );

        // Generar nuevo refresh token
        String newRefreshToken = jwtService.generateRefreshToken(
                user.username,
                user.id.toString()
        );

        // Actualizar refresh token en la base de datos
        user.refreshToken = newRefreshToken;
        user.refreshTokenExpiry = LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenDuration());
        userRepository.update(user);

        return new TokenDTO(
                newAccessToken,
                newRefreshToken,
                jwtService.getAccessTokenDuration()
        );
    }

    public void logout(String userId) {
        User user = userRepository.findByIdOptional(new ObjectId(userId))
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        user.refreshToken = null;
        user.refreshTokenExpiry = null;
        userRepository.update(user);
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.id.toString());
        dto.setUsername(user.username);
        dto.setEmail(user.email);
        dto.setFirstName(user.firstName);
        dto.setLastName(user.lastName);
        dto.setRoles(user.roles);
        dto.setCreatedAt(user.createdAt);
        dto.setUpdatedAt(user.updatedAt);
        return dto;
    }
}
