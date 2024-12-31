package com.one.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.one.eenum.PrizeCategory;

import java.io.IOException;

public class PrizeCategoryDeserializer extends JsonDeserializer<PrizeCategory> {
    @Override
    public PrizeCategory deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String description = p.getText();
        return PrizeCategory.fromDescription(description);
    }
}
