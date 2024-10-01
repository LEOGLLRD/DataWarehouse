package com.stockchain.dto.requests;

import lombok.Data;

@Data
public class UserSignUpRequest {
    String name;
    String email;
    String password;
}
