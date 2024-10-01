package com.stockchain.dto.responses;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Data
public class GetFileResponse extends Response{

    private List<String> files;

}
