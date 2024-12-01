package com.stockchain.entity;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.Data;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.google.gson.*;

import java.io.StringReader;
import java.util.HashMap;
import java.util.TreeMap;

@Data
@Getter
@Setter
@Document(collection = "home")
public class Home {

    @Id
    @Generated
    private String id;
    private String userId;
    private String home;


}