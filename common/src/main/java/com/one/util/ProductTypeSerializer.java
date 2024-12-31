package com.one.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.one.frontend.eenum.ProductType;

import java.io.IOException;

public class ProductTypeSerializer extends JsonSerializer<ProductType> {
    @Override
    public void serialize(ProductType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getDescription());
    }
}

