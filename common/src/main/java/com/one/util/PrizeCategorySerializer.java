package com.one.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.one.eenum.PrizeCategory;

import java.io.IOException;

public class PrizeCategorySerializer extends JsonSerializer<PrizeCategory> {
    @Override
    public void serialize(PrizeCategory value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.getDescription());
    }
}
