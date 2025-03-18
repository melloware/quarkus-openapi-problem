package io.quarkiverse.openapi.problem.jsonb;

import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import jakarta.json.Json;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

import org.eclipse.parsson.JsonProviderImpl;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkiverse.openapi.problem.HttpProblemMother;

class JsonbProblemSerializerTest {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    JsonGenerator jsonGenerator = Json.createGenerator(outputStream);
    SerializationContext context = new SerializationContextImpl(new JsonbContext(new JsonbConfig(), new JsonProviderImpl()));

    JsonbProblemSerializer serializer = new JsonbProblemSerializer();

    @Test
    @DisplayName("Should serialize all provided fields")
    void shouldSerializeAllFields() {
        HttpProblem problem = HttpProblemMother.complexProblem().build();

        serializer.serialize(problem, jsonGenerator, context);

        assertThat(serializedProblem())
                .isEqualTo(HttpProblemMother.SERIALIZED_COMPLEX_PROBLEM);
    }

    @Test
    @DisplayName("Should serialize only not null fields")
    void shouldSerializeOnlyNotNullFields() {
        HttpProblem problem = HttpProblemMother.badRequestProblem();

        serializer.serialize(problem, jsonGenerator, context);

        assertThat(serializedProblem())
                .isEqualTo(HttpProblemMother.SERIALIZED_BAD_REQUEST_PROBLEM);
    }

    @Test
    @DisplayName("Should decode uri for instance field")
    void shouldDecodeUriForInstanceField() {
        HttpProblem problem = HttpProblem.builder()
                .withStatus(NOT_FOUND.getStatusCode())
                .withInstance(URI.create("%2Fnon%7Cexisting%7Bpath+%2Fwith%7Bunwise%5Ccharacters%3E%23"))
                .build();

        serializer.serialize(problem, jsonGenerator, null);

        assertThat(serializedProblem()).contains("""
                "instance":"/non|existing{path /with{unwise\\\\characters>#"}""");
    }

    private String serializedProblem() {
        jsonGenerator.close();
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Should serialize single nested cause field")
    void shouldSerializeOnlySingleNestedProblem() {

        // Result of json conversion
        String result = """
                {"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"},"cause":{"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"}}}
                """;

        // Get the HttpProblem to test
        HttpProblem problem = HttpProblemMother.singleNestedProblem().build();

        // Serialize
        serializer.serialize(problem, jsonGenerator, context);

        // Compare results
        assertThat(serializedProblem().trim())
                .isEqualTo(result.trim());
    }

    @Test
    @DisplayName("Should serialize double nested cause field")
    void shouldSerializeDoubleNestedProblem() {

        // Result of json conversion
        String result = """
                {"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"},"cause":{"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"},"cause":{"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"}}}}
                """;

        // Get the HttpProblem to test
        HttpProblem problem = HttpProblemMother.doubleNessProblem().build();

        // Serialize
        serializer.serialize(problem, jsonGenerator, context);

        // Compare results
        assertThat(serializedProblem().trim())
                .isEqualTo(result.trim());
    }
}