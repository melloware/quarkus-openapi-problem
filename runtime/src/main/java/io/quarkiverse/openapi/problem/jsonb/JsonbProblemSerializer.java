package io.quarkiverse.openapi.problem.jsonb;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkiverse.openapi.problem.InstanceUtils;
import io.quarkiverse.openapi.problem.validation.Violation;

/**
 * Low level JsonB serializer for Problem type.
 */
public final class JsonbProblemSerializer implements JsonbSerializer<HttpProblem> {

    @Override
    public void serialize(HttpProblem problem, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();

        // Write standard RFC 9457 fields
        if (problem.getType() != null) {
            generator.write("type", problem.getType().toASCIIString());
        }
        generator.write("status", problem.getStatus());
        if (problem.getTitle() != null) {
            generator.write("title", problem.getTitle());
        }
        if (problem.getDetail() != null) {
            generator.write("detail", problem.getDetail());
        }
        if (problem.getInstance() != null) {
            generator.write("instance", InstanceUtils.instanceToPath(problem.getInstance()));
        }

        // Write additional parameters as "context"
        if (problem.getContexts() != null && !problem.getContexts().isEmpty()) {
            generator.writeKey("context");
            generator.writeStartObject();
            problem.getContexts().forEach((key, value) -> ctx.serialize(key, value, generator));
            generator.writeEnd();
        }

        // Write validation errors if present
        if (problem.getErrors() != null && !problem.getErrors().isEmpty()) {
            generator.writeKey("errors");
            generator.writeStartArray();
            for (Violation violation : problem.getErrors()) {
                ctx.serialize(violation, generator);
            }
            generator.writeEnd();
        }

        // Serialize the cause recursively
        if (problem.getCause() != null) {
            generator.writeKey("cause");
            serialize(problem.getCause(), generator, ctx);
        }

        generator.writeEnd();
    }
}