package com.stockchain.dto.responses;

import lombok.Data;

@Data
public class GetFilesSizeResponse extends Response {
    long filesSize;
}
