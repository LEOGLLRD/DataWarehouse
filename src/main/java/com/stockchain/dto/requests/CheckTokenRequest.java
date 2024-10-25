package com.stockchain.dto.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CheckTokenRequest {

    private String token;

}
