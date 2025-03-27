package io.quarkiverse.openapi.problem.test.server.service_one;

import io.quarkiverse.openapi.problem.HttpProblem;
import jakarta.ws.rs.*;

/**
 * Simulates a backend service
 */
@Path("/server/svc1")
public class ServiceOneResource {

    //    /**
    //     * Used to call downstream service
    //     */
    //    @Inject
    //    @RestClient
    //    ServiceTwoClient svcTwoClient;

    /**
     * Simulate a POST that fails and throws a not found
     * exception which could represent a missing entity, etc
     */
    @Path("/notFound")
    @POST
    public void throwNowFound() {
        throw new NotFoundException();
    }

    /**
     * Simulate a downstream error being called from this resource
     * and wrap it with our custom HttpProblem filling in the details
     */
    @Path("/badRequest")
    @POST
    public void throwBadRequest() {
        try {
            //svcTwoClient.badRequest();
        } catch (HttpProblem e) {

            // We build a custom error
            // and pass along the original
            // error upstream
            throw HttpProblem.builder()
                    .withHeader("X-Failure-Reason", "ON_PURPOSE")
                    .withCause(e)
                    .withDetail("Service Two Client Error")
                    .withStatus(422)
                    .withTitle("Could not complete request")
                    .build();
        }
    }
}
