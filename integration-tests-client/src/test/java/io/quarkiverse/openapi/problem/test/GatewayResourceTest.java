package io.quarkiverse.openapi.problem.test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.net.URI;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkus.test.junit.QuarkusTest;

/**
 * Programmatically tests the response from the services
 * to verify we are getting proper HttpProblem instances
 */
@QuarkusTest
class GatewayResourceTest {

    /**
     * Test direct call to service two Not Found endpoint
     */
    @Test
    void testServiceTwoNotFound() {
        HttpProblem prob = given()
                .when().post("/gateway/svc2/notFound")
                .then()
                .statusCode(404)
                .extract()
                .body()
                .as(HttpProblem.class);

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

    /**
     * Test direct call to service one Not Found endpoint
     */
    @Test
    void testServiceOneNotFound() {
        HttpProblem prob = given()
                .when().post("/gateway/svc1/notFound")
                .then()
                .statusCode(404)
                .extract()
                .body()
                .as(HttpProblem.class);

        assertThat(prob.getType(), equalTo(null));
        assertThat(prob.getStatus(), equalTo(404));
        assertThat(prob.getDetail(), equalTo("HTTP 404 Not Found"));
        assertThat(prob.getTitle(), equalTo("Not Found"));
        assertThat(prob.getInstance(), equalTo(URI.create("/server/svc1/notFound")));
        assertThat(prob.getCause(), equalTo(null));
        assertThat(prob.getHeaders(), equalTo(Collections.EMPTY_MAP));
        assertThat(prob.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));

    }

    /**
     * Test direct call to service two
     */
    @Test
    void testServiceTwoBadRequest() {
        HttpProblem prob = given()
                .when().post("/server/svc2/badRequest")
                .then()
                .statusCode(400)
                .extract()
                .body()
                .as(HttpProblem.class);

        assertThat(prob.getType(), equalTo(null));
        assertThat(prob.getStatus(), equalTo(400));
        assertThat(prob.getDetail(), equalTo("HTTP 400 Bad Request"));
        assertThat(prob.getTitle(), equalTo("Bad Request"));
        assertThat(prob.getInstance(), equalTo(URI.create("/server/svc2/badRequest")));
        assertThat(prob.getCause(), equalTo(null));
        assertThat(prob.getCause(), equalTo(null));
        assertThat(prob.getHeaders(), equalTo(Collections.EMPTY_MAP));
        assertThat(prob.getContexts().keySet(), containsInAnyOrder("X-Request-Address", "X-Request-Id"));
    }

    /**
     * Test direct response from service 1 for bad request which will
     * include a "cause" HttpProblem
     */
    @Test
    void testServiceOneBadRequest() {
        HttpProblem prob = given()
                .when().post("/server/svc1/badRequest")
                .then()
                .statusCode(422)
                .extract()
                .body()
                .as(HttpProblem.class);

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

    }

    /**
     * Test a full end to end failure that should return multiple
     * embedded HttpProblems
     */
    @Test
    void testGatewayCascadeError() {
        HttpProblem prob = given()
                .when().post("/gateway/cascadeError")
                .then()
                .statusCode(417)
                .extract()
                .body()
                .as(HttpProblem.class);

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

    }

}
