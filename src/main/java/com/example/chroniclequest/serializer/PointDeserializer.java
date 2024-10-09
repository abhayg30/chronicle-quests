package com.example.chroniclequest.serializer;

import com.example.chroniclequest.util.StringUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.geo.Point;

import java.io.IOException;

public class PointDeserializer extends JsonDeserializer<Point> {

    @Override
    public Point deserialize(JsonParser p, DeserializationContext context)
            throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        JsonNode location = node.get(StringUtils.SERIALIZER_DESERIALIZER_LOCATION);
        double lon = location.get(0).asDouble();
        double lat = location.get(1).asDouble();
        Point pt = new Point(lon, lat);
        return pt;
    }

}
