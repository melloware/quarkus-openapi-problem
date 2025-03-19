package io.quarkiverse.openapi.problem.test.client;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * REST client interface for interacting with Service Two.
 * This client provides endpoints to trigger specific HTTP error responses
 * and retrieve a greeting message.
 *
 * <p>
 * It is registered as a MicroProfile Rest Client with the configuration key <b>"service-two"</b>.
 * The base URL for this client should be specified in the application configuration
 * using the property:
 *
 * <pre>
 * quarkus.rest-client.service-one.url=http://localhost:{quarkus.http.port}/server/svc2
 * </pre>
 *
 * @see RegisterRestClient
 */
@RegisterRestClient(configKey = "service-two")
public interface ServiceTwoClient {

    /**
     * Sends a POST request to trigger a <b>404 Not Found</b> error response.
     * This method does not return a response body.
     *
     * <p>
     * Endpoint: <code>POST /notFound</code>
     * </p>
     *
     * @throws io.quarkiverse.openapi.problem.HttpProblem which is then parsed by response mapper
     */
    @POST
    @Path("/notFound")
    void notFound();

    /**
     * Sends a POST request to trigger a <b>400 Bad Request</b> error response.
     * This method does not return a response body.
     *
     * <p>
     * Endpoint: <code>POST /badRequest</code>
     * </p>
     *
     * @throws io.quarkiverse.openapi.problem.HttpProblem which is then parsed by response mapper
     */
    @POST
    @Path("/badRequest")
    void badRequest();

}
