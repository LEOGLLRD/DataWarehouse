package com.stockchain.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class SourceAdapter extends TypeAdapter<Source> {
    @Override
    public void write(JsonWriter jsonWriter, Source source) throws IOException {

    }

    @Override
    public Source read(JsonReader jsonReader) throws IOException {
        return null;
    }
}