package com.stockchain.dto.responses;

import lombok.Data;

@Data
public class Response {

    private int statusCode;
    private String error;
    private String message;

}
