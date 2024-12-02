package com.stockchain.dto.requests;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DepositFileRequest {

    private String name;
    private MultipartFile file;
    private String idUser;
    private String path;
}
