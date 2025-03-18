package io.quarkiverse.openapi.problem;

import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;

import org.zalando.problem.ThrowableProblem;

/**
 * Mapper for ThrowableProblem exception from Zalando Problem library.
 */
@Priority(Priorities.USER)
public final class ZalandoProblemMapper extends ExceptionMapperBase<ThrowableProblem> {

    @Override
    protected HttpProblem toProblem(ThrowableProblem exception) {
        Response.StatusType status = Optional.ofNullable(exception.getStatus())
                .map(problemStatus -> Response.Status.fromStatusCode(problemStatus.getStatusCode()))
                .orElse(Response.Status.INTERNAL_SERVER_ERROR);

        HttpProblem.HttpProblemBuilder builder = HttpProblem.builder()
                .withType(exception.getType())
                .withTitle(exception.getTitle())
                .withStatus(status.getStatusCode())
                .withDetail(exception.getDetail())
                .withInstance(exception.getInstance());
        exception.getParameters().forEach(builder::withContext);
        return builder.build();
    }

}