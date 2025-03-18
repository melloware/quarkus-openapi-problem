package io.quarkiverse.openapi.problem.it;

import java.util.List;
import java.util.UUID;

import io.quarkiverse.openapi.problem.HttpProblem;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/throw/json/")
@Consumes(MediaType.APPLICATION_JSON)
public class JsonExceptionsResource {

    @POST
    @Operation(summary = "Throw JSON exception", description = "Throw JSON exception")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Title retrieved successfully"),
            @APIResponse(responseCode = "404", description = "Application title not found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = HttpProblem.class)))
    })
    public void throwValidationException(TestRequestBody body) {
    }

    public static final class TestRequestBody {
        public UUID uuid_field_1;

        public Nested nested;

        public List<Nested> collection;

        public static final class Nested {
            public UUID uuid_field_2;
        }
    }

}