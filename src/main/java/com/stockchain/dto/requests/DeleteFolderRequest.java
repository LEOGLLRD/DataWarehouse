package com.stockchain.dto.requests;

import lombok.Data;

@Data
public class DeleteFolderRequest {
    String idUser;
    String folderName;
    String path;
}
