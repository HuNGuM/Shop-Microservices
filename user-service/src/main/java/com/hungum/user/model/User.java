package com.hungum.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "User")
@Data
public class User {
    @Id
    private String id;
    @Email
    @NotEmpty(message = "Email is required")
    private String email;
    @NotEmpty(message = "Email is required")
    private String username;
    @NotEmpty(message = "Password is required")
    private String password;
    @Transient
    @NotEmpty(message = "Password Confirmation is Required")
    private String passwordConfirmation;
    private boolean enabled;

    public User(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
