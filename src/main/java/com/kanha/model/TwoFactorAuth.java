package com.kanha.model;

import com.kanha.domain.Verification_Type;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnabled =false;
    private Verification_Type sendTo ;
}
