package com.stockchain.dto.responses;

import com.stockchain.entity.File;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class GetFileResponse extends Response {

    private File file;

}
