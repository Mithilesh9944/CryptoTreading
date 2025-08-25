package com.kanha.request;

import com.kanha.domain.Verification_Type;
import lombok.Data;

@Data
public class ForgotPasswordTokenRequest {
    private String sendTo;
    private Verification_Type verificationType;
}
