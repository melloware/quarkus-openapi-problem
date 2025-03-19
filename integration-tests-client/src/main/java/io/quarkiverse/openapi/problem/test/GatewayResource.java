package io.quarkiverse.openapi.problem.test;

import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkiverse.openapi.problem.test.client.ServiceOneClient;
import io.quarkiverse.openapi.problem.test.client.ServiceTwoClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Gateway which will call a remote endpoint and
 * return the results and shows how to use the
 * `HttpProblemClientExceptionMapper` to parse the
 * incoming Response into a HttpProblem.
 */
@Path("/gateway")
public class GatewayResource {

    @Inject
    @RestClient
    ServiceOneClient svcOneClient;

    @Inject
    @RestClient
    ServiceTwoClient svcTwoClient;

    /**
     * Return hello from gateway
     *
     * @return
     */
    @Path("/hello")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Gateway!";
    }

    /**
     * Call the remote service which will throw a NotFound
     * error during processing simulating a record not being
     * found
     * <br>
     * This example does not modify the incoming error
     * from the backend and passes it as is
     */
    @Path("/svc1/notFound")
    @POST
    @Operation(summary = "Simulate a downstream not found", description = "Shows propagation of errors")
    @APIResponses({
            @APIResponse(responseCode = "404", description = "Expected")
    })
    public void svc1_notFound() {
        svcOneClient.notFound();
    }

    /**
     * Call the remote service which will throw a NotFound
     * error during processing simulating a record not being
     * found
     * <br>
     * This example does not modify the incoming error
     * from the backend and passes it as is
     */
    @Path("/svc2/notFound")
    @POST
    @Operation(summary = "Simulate a downstream not found", description = "Shows propagation of errors")
    @APIResponses({
            @APIResponse(responseCode = "404", description = "Expected")
    })
    public void svc2_notFound() {
        svcTwoClient.notFound();
    }

    /**
     * Call the remote service(s) which will throw a BadRequest
     * error during processing simulating a record not being
     * found
     * <br>
     * This example show how you can catch the HttpProblem
     * and enhance it with details and set the error into
     * the new one
     */
    @Path("/cascadeError")
    @POST
    @Operation(summary = "Simulate a downstream errors", description = "Shows propagation of errors across services")
    @APIResponses({
            @APIResponse(responseCode = "417", description = "Expected")
    })
    public void cascadeError() {
        try {
            // This will simulate calling one remote service which in turn
            // calls another remote service and the whole thing returns
            // and HttpProblem showing how the original errors can be
            // manually returned
            svcOneClient.badRequest();
        } catch (HttpProblem e) {

            // We build a custom error
            // and pass along the original
            // error upstream
            throw HttpProblem.builder()
                    .withHeaders(e.getHeaders())
                    .withCause(e)
                    .withDetail("Aw Shucks!")
                    .withStatus(417)
                    .withTitle("I didn't like your request")
                    .build();
        }
    }
}
