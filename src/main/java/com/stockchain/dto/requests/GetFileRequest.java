package com.stockchain.dto.requests;

import lombok.Data;

@Data
public class GetFileRequest {

    String userName;
    String fileName;
    String path;

}
