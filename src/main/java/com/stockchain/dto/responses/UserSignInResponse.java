package com.stockchain.dto.responses;

import lombok.Data;

@Data
public class UserSignInResponse extends Response {

    String token;
    private String refreshToken;
    private String expirationTime;

}
