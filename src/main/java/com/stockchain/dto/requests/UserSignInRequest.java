package com.stockchain.dto.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockchain.entity.User;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserSignInRequest {

    private String email;
    private String password;

}
