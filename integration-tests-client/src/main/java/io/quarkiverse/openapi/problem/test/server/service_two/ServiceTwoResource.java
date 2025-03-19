package io.quarkiverse.openapi.problem.test.server.service_two;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Simulates a backend service that is called by service one
 */
@Path("/server/svc2")
public class ServiceTwoResource {

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
     */
    @Path("/badRequest")
    @POST
    public void throwBadRequest() {
        throw new BadRequestException();
    }
}
