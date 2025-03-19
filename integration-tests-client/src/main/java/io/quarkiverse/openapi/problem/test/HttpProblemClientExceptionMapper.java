package io.quarkiverse.openapi.problem.test;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import io.quarkiverse.openapi.problem.HttpProblem;

/**
 * Provider used in MicroProfile REST Client applications to handle HttpProblem responses between API calls.
 * When one service calls another service using MicroProfile REST Client, this mapper will:
 * <ul>
 * <li>Check if the response contains an HttpProblem (via application/problem+json media type)</li>
 * <li>If found, deserialize the response into an HttpProblem object</li>
 * <li>Re-throw the HttpProblem, preserving the original error details across service boundaries</li>
 * </ul>
 * This enables consistent error handling and propagation in microservice architectures.
 */
@Provider
public class HttpProblemClientExceptionMapper implements ResponseExceptionMapper<RuntimeException> {

    @Override
    public RuntimeException toThrowable(Response response) {
        if (!HttpProblem.APPLICATION_JSON_PROBLEM_TYPE.equals(response.getMediaType())) {
            return null; // Let others handle non-problem formats
        }

        try {
            return response.readEntity(HttpProblem.class);
        } catch (Exception e) {
            return new RuntimeException(e);
        }
    }

    /**
     * We want this to run first before the other HttpProblem provider
     */
    @Override
    public int getPriority() {
        return Priorities.USER - 100;
    }
}
