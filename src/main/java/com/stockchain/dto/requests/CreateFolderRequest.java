package com.stockchain.dto.requests;

import lombok.Data;

@Data
public class CreateFolderRequest {

    String folderName;
    String path;
    String idUser;

}
