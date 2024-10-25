package com.stockchain.entity;

import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@Setter
@Document(collection = "file")
public class File {

    @Id
    @Generated
    private Integer id;
    private String filename;
    private String userEmail;
    private String fileType;
    private String fileSize;
    private byte[] file;


}
