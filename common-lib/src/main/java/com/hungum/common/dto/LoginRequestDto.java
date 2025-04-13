package com.hungum.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class LoginRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
