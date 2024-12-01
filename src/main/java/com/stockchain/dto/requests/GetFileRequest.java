package com.stockchain.dto.requests;

import lombok.Data;

@Data
public class GetFileRequest {

    String idUser;
    String fileName;
    String path;

}
