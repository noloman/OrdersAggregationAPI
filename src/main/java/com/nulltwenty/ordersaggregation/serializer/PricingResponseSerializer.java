package com.nulltwenty.ordersaggregation.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nulltwenty.ordersaggregation.model.PricingResponse;

import java.io.IOException;

public class PricingResponseSerializer extends StdSerializer<PricingResponse> {

    public PricingResponseSerializer() {
        this(null);
    }

    public PricingResponseSerializer(Class<PricingResponse> t) {
        super(t);
    }

    @Override
    public void serialize(PricingResponse pricingResponse, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(pricingResponse.getCountryCode(), String.valueOf(pricingResponse.getPrice().doubleValue()));
        jgen.writeEndObject();
    }
}
