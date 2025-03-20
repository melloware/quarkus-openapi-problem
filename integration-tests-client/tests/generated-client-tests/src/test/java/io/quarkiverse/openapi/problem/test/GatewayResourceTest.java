package io.quarkiverse.openapi.problem.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

import io.quarkiverse.openapi.client.gateway.api.GatewayResourceApi;
import io.quarkiverse.openapi.client.serviceOne.api.ServiceOneResourceApi;
import io.quarkiverse.openapi.client.serviceTwo.api.ServiceTwoResourceApi;
import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.net.URI;
import java.util.Collections;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.junit.jupiter.api.Test;

/**
 * Programmatically tests the response from the services
 * to verify we are getting proper HttpProblem instances
 */
@QuarkusTest
class GatewayResourceTest {

    @Inject
    @RestClient
    GatewayResourceApi gatewayResourceApi;

    @Inject
    @RestClient
    ServiceOneResourceApi serviceOneResourceApi;

    @Inject
    @RestClient
    ServiceTwoResourceApi serviceTwoResourceApi;

    /**
     * Test direct call to service two Not Found endpoint
     */
    @Test
    void testServiceTwoNotFound() {

        try {
            serviceTwoResourceApi.serverSvc2NotFoundPost();
            fail("Should have thrown an exception");
        } catch (ClientWebApplicationException ex) {

            HttpProblem prob = ex.getResponse().readEntity(HttpProblem.class);

            assertThat(prob.getType(), equalTo(null));
            assertThat(prob.getStatus(), equalTo(404));
            assertThat(prob.getDetail(), equalTo("HTTP 404 Not Found"));
            assertThat(prob.getTitle(), equalTo("Not Found"));
            assertThat(prob.getInstance(), equalTo(URI.create("/server/svc2/notFound")));
            assertThat(prob.getCause(), equalTo(null));
            assertThat(prob.getCause(), equalTo(null));
            assertThat(prob.getHeaders(), equalTo(Collections.EMPTY_MAP));
            assertThat(prob.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));
        }
    }

    /**
     * Test direct call to service one Not Found endpoint
     */
    @Test
    void testServiceOneNotFound() {

        try {
            serviceOneResourceApi.serverSvc1NotFoundPost();
            fail("Should have thrown an exception");
        } catch (ClientWebApplicationException ex) {

            HttpProblem prob = ex.getResponse().readEntity(HttpProblem.class);
            assertThat(prob.getType(), equalTo(null));
            assertThat(prob.getStatus(), equalTo(404));
            assertThat(prob.getDetail(), equalTo("HTTP 404 Not Found"));
            assertThat(prob.getTitle(), equalTo("Not Found"));
            assertThat(prob.getInstance(), equalTo(URI.create("/server/svc1/notFound")));
            assertThat(prob.getCause(), equalTo(null));
            assertThat(prob.getHeaders(), equalTo(Collections.EMPTY_MAP));
            assertThat(prob.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));
        } catch (Exception e) {
            fail("Should have thrown an HttpProblem exception");
        }
    }

    /**
     * Test direct call to service two
     */
    @Test
    void testServiceTwoBadRequest() {

        try {
            serviceTwoResourceApi.serverSvc2BadRequestPost();
            fail("Should have thrown an exception");
        } catch (ClientWebApplicationException ex) {

            HttpProblem prob = ex.getResponse().readEntity(HttpProblem.class);
            assertThat(prob.getType(), equalTo(null));
            assertThat(prob.getStatus(), equalTo(400));
            assertThat(prob.getDetail(), equalTo("HTTP 400 Bad Request"));
            assertThat(prob.getTitle(), equalTo("Bad Request"));
            assertThat(prob.getInstance(), equalTo(URI.create("/server/svc2/badRequest")));
            assertThat(prob.getCause(), equalTo(null));
            assertThat(prob.getCause(), equalTo(null));
            assertThat(prob.getHeaders(), equalTo(Collections.EMPTY_MAP));
            assertThat(prob.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));
        } catch (Exception e) {
            fail("Should have thrown an HttpProblem exception");
        }
    }

    /**
     * Test direct response from service 1 for bad request which will
     * include a "cause" HttpProblem
     */
    @Test
    void testServiceOneBadRequest() {
        try {
            serviceOneResourceApi.serverSvc1BadRequestPost();
        } catch (ClientWebApplicationException ex) {

            HttpProblem prob = ex.getResponse().readEntity(HttpProblem.class);
            assertThat(prob.getType(), equalTo(null));
            assertThat(prob.getStatus(), equalTo(422));
            assertThat(prob.getDetail(), equalTo("Service Two Client Error"));
            assertThat(prob.getTitle(), equalTo("Could not complete request"));
            assertThat(prob.getInstance(), equalTo(URI.create("/server/svc1/badRequest")));
            assertThat(prob.getHeaders(), equalTo(Collections.EMPTY_MAP));
            assertThat(prob.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));

            // This test should have an embedded problem in it
            // and verify the fields
            HttpProblem cause = prob.getCause();
            assertThat(cause, not(equalTo(null)));
            assertThat(cause.getType(), equalTo(null));
            assertThat(cause.getStatus(), equalTo(400));
            assertThat(cause.getDetail(), equalTo("HTTP 400 Bad Request"));
            assertThat(cause.getTitle(), equalTo("Bad Request"));
            assertThat(cause.getInstance(), equalTo(URI.create("/server/svc2/badRequest")));
            assertThat(cause.getCause(), equalTo(null));
            assertThat(cause.getHeaders(), equalTo(Collections.EMPTY_MAP));
            assertThat(cause.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));
        } catch (Exception e) {
            fail("Should have thrown an HttpProblem exception");
        }
    }

    /**
     * Test a full end to end failure that should return multiple
     * embedded HttpProblems
     */
    @Test
    void testGatewayCascadeError() {
        try {
            gatewayResourceApi.gatewayCascadeErrorPost();
        } catch (ClientWebApplicationException ex) {

            HttpProblem prob = ex.getResponse().readEntity(HttpProblem.class);
            assertThat(prob.getType(), equalTo(null));
            assertThat(prob.getStatus(), equalTo(417));
            assertThat(prob.getDetail(), equalTo("Aw Shucks!"));
            assertThat(prob.getTitle(), equalTo("I didn't like your request"));
            assertThat(prob.getInstance(), equalTo(URI.create("/gateway/cascadeError")));
            assertThat(prob.getHeaders(), equalTo(Collections.EMPTY_MAP));
            assertThat(prob.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));

            // This test should have an embedded problem in it
            // and verify the fields
            HttpProblem cause = prob.getCause();
            assertThat(cause, not(equalTo(null)));
            assertThat(cause.getType(), equalTo(null));
            assertThat(cause.getStatus(), equalTo(422));
            assertThat(cause.getDetail(), equalTo("Service Two Client Error"));
            assertThat(cause.getTitle(), equalTo("Could not complete request"));
            assertThat(cause.getInstance(), equalTo(URI.create("/server/svc1/badRequest")));
            assertThat(cause.getHeaders(), equalTo(Collections.EMPTY_MAP));
            assertThat(cause.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));

            // Should have another cause inside the original problem
            // and this one should NOT have any other embedded ones
            cause = cause.getCause();
            assertThat(cause, not(equalTo(null)));
            assertThat(cause.getCause(), equalTo(null));
            assertThat(cause.getType(), equalTo(null));
            assertThat(cause.getStatus(), equalTo(400));
            assertThat(cause.getDetail(), equalTo("HTTP 400 Bad Request"));
            assertThat(cause.getTitle(), equalTo("Bad Request"));
            assertThat(cause.getInstance(), equalTo(URI.create("/server/svc2/badRequest")));
            assertThat(cause.getHeaders(), equalTo(Collections.EMPTY_MAP));
            assertThat(cause.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));
        } catch (Exception e) {
            fail("Should have thrown an HttpProblem exception");
        }
    }

}
