package com.kanha.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kanha.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String email;
    //want to accept a password in a request but avoid exposing it
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Embedded
    private TwoFactorAuth twoFactorAuth= new TwoFactorAuth();

    private USER_ROLE role= USER_ROLE.CUSTOMER;

}
