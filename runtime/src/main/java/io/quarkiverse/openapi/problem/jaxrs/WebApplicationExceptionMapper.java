package io.quarkiverse.openapi.problem.jaxrs;

import io.quarkiverse.openapi.problem.ExceptionMapperBase;
import io.quarkiverse.openapi.problem.HttpProblem;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.Optional;

/**
 * Generic exception mapper for JaxRS WebApplicationExceptions - it passes status and message to application/problem response.
 */
@Priority(Priorities.USER)
public final class WebApplicationExceptionMapper extends ExceptionMapperBase<WebApplicationException> {

    @Override
    protected HttpProblem toProblem(WebApplicationException exception) {
        Response.StatusType status = exception.getResponse().getStatusInfo();

        HttpProblem.HttpProblemBuilder problem = HttpProblem.builder()
                .withTitle(status.getReasonPhrase())
                .withStatus(status.getStatusCode())
                .withDetail(exception.getMessage());

        Optional.ofNullable(exception.getResponse().getHeaders())
                .ifPresent(headers -> {
                    headers.forEach((header, values) -> values.forEach(value -> problem.withHeader(header, value)));
                });

        return problem.build();
    }
}
