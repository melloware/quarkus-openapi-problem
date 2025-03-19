package io.quarkiverse.openapi.problem.jackson;

import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkiverse.openapi.problem.ExceptionMapperBase;
import io.quarkiverse.openapi.problem.HttpProblem;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;

/**
 * Mapper for Jackson payload processing exceptions.
 */
@Priority(Priorities.USER)
public final class JsonProcessingExceptionMapper extends ExceptionMapperBase<JsonProcessingException> {

    @Override
    protected HttpProblem toProblem(JsonProcessingException exception) {
        return HttpProblem.valueOf(BAD_REQUEST, exception.getOriginalMessage());
    }
}
