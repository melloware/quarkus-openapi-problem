package io.quarkiverse.openapi.problem.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkiverse.openapi.problem.HttpProblemMother;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JacksonProblemSerializerTest {

    private ByteArrayOutputStream outputStream;
    private JsonGenerator jsonGenerator;
    private JacksonProblemSerializer serializer;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() throws IOException {
        outputStream = new ByteArrayOutputStream();
        mapper = new ObjectMapper(); // Create ObjectMapper
        jsonGenerator = new JsonFactory().createGenerator(outputStream, JsonEncoding.UTF8);
        jsonGenerator.setCodec(mapper); // Set ObjectCodec to avoid IllegalStateException
        serializer = new JacksonProblemSerializer();

        SimpleModule module = new SimpleModule();
        module.addSerializer(HttpProblem.class, serializer);

        mapper.registerModule(module);
    }

    @Test
    @DisplayName("Should serialize all provided fields")
    void shouldSerializeAllFields() throws IOException {
        HttpProblem problem = HttpProblemMother.complexProblem().build();

        serializer.serialize(problem, jsonGenerator, mapper.getSerializerProvider());

        assertThat(serializedProblem()).isEqualTo(HttpProblemMother.SERIALIZED_COMPLEX_PROBLEM);
    }

    @Test
    @DisplayName("Should serialize only not null fields")
    void shouldSerializeOnlyNotNullFields() throws IOException {
        HttpProblem problem = HttpProblemMother.badRequestProblem();

        serializer.serialize(problem, jsonGenerator, mapper.getSerializerProvider());

        assertThat(serializedProblem()).isEqualTo(HttpProblemMother.SERIALIZED_BAD_REQUEST_PROBLEM);
    }

    @Test
    @DisplayName("Should decode URI for instance field")
    void shouldDecodeUriForInstanceField() throws IOException {
        HttpProblem problem = HttpProblem.builder()
                .withStatus(404)
                .withInstance(URI.create("%2Fnon%7Cexisting%7Bpath+%2Fwith%7Bunwise%5Ccharacters%3E%23"))
                .build();

        serializer.serialize(problem, jsonGenerator, mapper.getSerializerProvider());

        assertThat(serializedProblem()).contains("""
                "instance":"/non|existing{path /with{unwise\\\\characters>#"}""");
    }

    @Test
    @DisplayName("Should serialize single nested cause field")
    void shouldSerializeOnlySingleNestedProblem() throws IOException {
        String result = """
                {"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"},"cause":{"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"}}}
                """;

        HttpProblem problem = HttpProblemMother.singleNestedProblem().build();
        serializer.serialize(problem, jsonGenerator, mapper.getSerializerProvider());

        assertThat(serializedProblem().trim()).isEqualTo(result.trim());
    }

    @Test
    @DisplayName("Should serialize double nested cause field")
    void shouldSerializeDoubleNestedProblem() throws IOException {
        String result = """
                {"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"},"cause":{"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"},"cause":{"type":"http://tietoevry.com/problem","status":400,"title":"Something wrong in the dirt","detail":"Deep down wrongness, zażółć gęślą jaźń for Håkensth","instance":"/endpoint","context":{"custom_field_1":"too long","custom_field_2":"too short"}}}}
                """;

        HttpProblem problem = HttpProblemMother.doubleNessProblem().build();
        serializer.serialize(problem, jsonGenerator, mapper.getSerializerProvider());

        assertThat(serializedProblem().trim()).isEqualTo(result.trim());
    }

    private String serializedProblem() throws IOException {
        jsonGenerator.flush(); // Ensure all JSON is written
        jsonGenerator.close(); // Close the stream after flushing
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
