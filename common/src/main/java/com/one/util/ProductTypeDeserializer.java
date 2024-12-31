package com.one.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.one.frontend.eenum.ProductType;

import java.io.IOException;

public class ProductTypeDeserializer extends JsonDeserializer<ProductType> {
    @Override
    public ProductType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String description = p.getText();
        return ProductType.fromDescription(description);
    }
}

