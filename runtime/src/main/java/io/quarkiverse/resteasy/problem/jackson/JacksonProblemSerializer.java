package io.quarkiverse.resteasy.problem.jackson;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkiverse.resteasy.problem.InstanceUtils;

/**
 * Low level Jackson serializer for HttpProblem type.
 * Follows RFC 9457 for HTTP Problem responses.
 */
public final class JacksonProblemSerializer extends StdSerializer<HttpProblem> {

    public JacksonProblemSerializer() {
        this(null);
    }

    public JacksonProblemSerializer(Class<HttpProblem> t) {
        super(t);
    }

    @Override
    public void serialize(final HttpProblem problem, final JsonGenerator json, final SerializerProvider serializers)
            throws IOException {
        json.writeStartObject();

        // Write standard RFC 9457 fields
        if (problem.getType() != null) {
            json.writeStringField("type", problem.getType().toASCIIString());
        }
        json.writeNumberField("status", problem.getStatus());
        if (problem.getTitle() != null) {
            json.writeStringField("title", problem.getTitle());
        }
        if (problem.getDetail() != null) {
            json.writeStringField("detail", problem.getDetail());
        }
        if (problem.getInstance() != null) {
            json.writeStringField("instance", InstanceUtils.instanceToPath(problem.getInstance()));
        }

        // Write context parameters if present
        if (problem.getContexts() != null && !problem.getContexts().isEmpty()) {
            json.writeFieldName("context");
            json.writeStartObject();
            for (Map.Entry<String, Object> entry : problem.getContexts().entrySet()) {
                json.writeObjectField(entry.getKey(), entry.getValue());
            }
            json.writeEndObject();
        }

        // Write validation errors if present
        if (problem.getErrors() != null && !problem.getErrors().isEmpty()) {
            json.writeObjectField("errors", problem.getErrors());
        }

        json.writeEndObject();
    }
}