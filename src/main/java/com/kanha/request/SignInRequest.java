package com.kanha.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Login request containing user email and password")
public class SignInRequest {
    @Schema(description = "Registered email of the user",example = "abhishekthakur@gmail.com")
    public String email;
    @Schema(description = "User Password",example = "abhishek@123")
    public String password;
}
