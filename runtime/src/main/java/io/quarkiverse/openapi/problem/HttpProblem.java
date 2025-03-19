package io.quarkiverse.openapi.problem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkiverse.openapi.problem.validation.Violation;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.SortedMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Represents an HTTP Problem Response according to RFC 9457. This class
 * encapsulates details about an error that occurred during HTTP request processing.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with", toBuilder = true)
@Jacksonized
@Schema(description = "HTTP Problem Response according to RFC 9457")
public class HttpProblem extends RuntimeException {

    /**
     * A {@code String} constant representing "application/problem+json" media type.
     */
    public static final String APPLICATION_JSON_PROBLEM = "application/problem+json";
    /**
     * A {@link MediaType} constant representing {@value #APPLICATION_JSON_PROBLEM} media type.
     */
    public static final MediaType APPLICATION_JSON_PROBLEM_TYPE = new MediaType("application", "problem+json");

    /** A URI reference that identifies the problem type */
    @Schema(description = "A URI reference that identifies the problem type", examples = "https://example.com/errors/validation")
    private URI type;

    /** A short, human-readable summary of the problem type */
    @Schema(description = "A short, human-readable summary of the problem type", examples = "Not Found Error")
    private String title;

    /** The HTTP status code for this occurrence of the problem */
    @Schema(description = "The HTTP status code for this occurrence of the problem", examples = "400")
    private int status;

    /** A human-readable explanation specific to this occurrence of the problem */
    @Schema(description = "A human-readable explanation specific to this occurrence of the problem", examples = "Record not found")
    private String detail;

    /** A URI reference that identifies the specific occurrence of the problem */
    @Schema(description = "A URI reference that identifies the specific occurrence of the problem", examples = "https://api.example.com/errors/123")
    private URI instance;

    /** Additional parameters providing more details about the problem */
    @Singular
    @JsonProperty("context")
    @Schema(description = "Additional parameters providing more details about the problem", examples = "{\"timestamp\":\"2024-03-20T10:00:00Z\",\"traceId\":\"550e8400-e29b-41d4-a716-446655440000\"}")
    private SortedMap<String, Object> contexts;

    /** HTTP headers associated with the problem response */
    @JsonIgnore
    @Singular
    private SortedMap<String, Object> headers;

    /** List of validation violations that occurred */
    @Schema(description = "List of validation constraint violations that occurred")
    private List<Violation> errors;

    /** Original cause of error, only set when forwarding an underlying problem */
    @Schema(description = "Original cause of error, only set when forwarding an underlying problem")
    private HttpProblem cause;

    public HttpProblem(HttpProblem problem) {
        this(problem.getType(), problem.getTitle(), problem.getStatus(), problem.getDetail(), problem.getInstance(),
                problem.getContexts(), problem.getHeaders(), problem.getErrors(), problem.getCause());
    }

    public Response toResponse() {
        Response.ResponseBuilder builder = Response
                .status(getStatus())
                .type(HttpProblem.APPLICATION_JSON_PROBLEM_TYPE)
                .entity(this);

        getHeaders().forEach(builder::header);

        return builder.build();
    }

    public static HttpProblem valueOf(Response.Status status) {
        return builder()
                .withTitle(status.getReasonPhrase())
                .withStatus(status.getStatusCode())
                .build();
    }

    public static HttpProblem valueOf(Response.Status status, String detail) {
        return builder()
                .withTitle(status.getReasonPhrase())
                .withStatus(status.getStatusCode())
                .withDetail(detail)
                .build();
    }

}
