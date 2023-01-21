package com.nulltwenty.ordersaggregation.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nulltwenty.ordersaggregation.model.AggregatedResponse;

import java.io.IOException;

public class AggregatedResponseSerializer extends StdSerializer<AggregatedResponse> {
    public AggregatedResponseSerializer() {
        this(null);
    }

    public AggregatedResponseSerializer(Class<AggregatedResponse> t) {
        super(t);
    }

    @Override
    public void serialize(AggregatedResponse aggregatedResponse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("shipments", aggregatedResponse.getShipments());
        jsonGenerator.writeObjectField("track", aggregatedResponse.getTrack());
        jsonGenerator.writeObjectField("pricing", aggregatedResponse.getPricing());
        jsonGenerator.writeEndObject();
    }
}
