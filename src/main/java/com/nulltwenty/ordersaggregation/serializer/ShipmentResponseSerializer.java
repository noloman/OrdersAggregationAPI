package com.nulltwenty.ordersaggregation.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nulltwenty.ordersaggregation.model.ShipmentsResponse;

import java.io.IOException;
import java.util.Arrays;

public class ShipmentResponseSerializer extends StdSerializer<ShipmentsResponse> {
    public ShipmentResponseSerializer() {
        this(null);
    }

    public ShipmentResponseSerializer(Class<ShipmentsResponse> t) {
        super(t);
    }

    @Override
    public void serialize(ShipmentsResponse shipmentsResponse, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(shipmentsResponse.getNumber(), Arrays.toString(shipmentsResponse.getShipmentStatus().toArray()));
        jgen.writeEndObject();
    }
}
