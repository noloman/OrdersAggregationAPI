package com.nulltwenty.ordersaggregation.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nulltwenty.ordersaggregation.model.TrackResponse;

import java.io.IOException;

public class TrackResponseSerializer extends StdSerializer<TrackResponse> {

    public TrackResponseSerializer() {
        this(null);
    }

    public TrackResponseSerializer(Class<TrackResponse> t) {
        super(t);
    }

    @Override
    public void serialize(TrackResponse trackResponse, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(trackResponse.number, trackResponse.value);
        jgen.writeEndObject();
    }
}
