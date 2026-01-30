package org.juanlopezaranzazu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDTO {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
}