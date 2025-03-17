package io.quarkiverse.resteasy.problem.postprocessing;

import static io.quarkiverse.resteasy.problem.postprocessing.ProblemContextMother.simpleContext;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkiverse.resteasy.problem.validation.Violation;

class ProblemLoggerTest {

    Logger logger = mock(Logger.class);
    ProblemLogger processor = new ProblemLogger(logger);

    @BeforeEach
    void init() {
        when(logger.isErrorEnabled()).thenReturn(true);
        when(logger.isInfoEnabled()).thenReturn(true);
    }

    @Test
    void shouldPrintOnlyNotNullFields() {
        HttpProblem problem = HttpProblem.builder()
                .withTitle("your fault")
                .withStatus(BAD_REQUEST.getStatusCode())
                .build();

        processor.apply(problem, simpleContext());

        verify(logger).info("status=400, title=\"your fault\"");
    }

    @Test
    void shouldPrintCustomParameters() {
        HttpProblem problem = HttpProblem.builder()
                .withTitle("your fault")
                .withStatus(BAD_REQUEST.getStatusCode())
                .withContext("custom-field", "123")
                .withErrors(Collections.singletonList(Violation.In.body.field("key").message("too small")))
                .withContext("nullable_field", null)
                .build();

        processor.apply(problem, simpleContext());

        verify(logger).info(
                "status=400, title=\"your fault\", custom-field=\"123\", nullable_field=null, errors=\"Violation(pointer=key, in=body, detail=too small)\"");
    }

    @Test
    void shouldPrintStackTraceFor500s() {
        HttpProblem problem = HttpProblem.builder()
                .withTitle("my fault")
                .withStatus(INTERNAL_SERVER_ERROR.getStatusCode())
                .build();
        RuntimeException cause = new RuntimeException("hey");

        processor.apply(problem, ProblemContextMother.withCause(cause));

        verify(logger).error("status=500, title=\"my fault\"", cause);
    }

}