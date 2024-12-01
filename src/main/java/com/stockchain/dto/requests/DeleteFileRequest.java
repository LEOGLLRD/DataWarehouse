package com.stockchain.dto.requests;

import lombok.Data;

@Data
public class DeleteFileRequest {
    String idUser;
    String fileName;
    String path;
}
