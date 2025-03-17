package io.quarkiverse.resteasy.problem;

import static io.restassured.RestAssured.given;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.BAD_REQUEST;
import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ValidationMappersIT {

    static final String SAMPLE_DETAIL = "A small one";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void validationExceptionShouldReturn500() {
        given()
                .queryParam("message", SAMPLE_DETAIL)
                .get("/throw/validation/validation-exception")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body("title", equalTo(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("status", equalTo(INTERNAL_SERVER_ERROR.getStatusCode()))
                .body("detail", nullValue())
                .body("stacktrace", nullValue());
    }

    @Test
    void constraintDeclarationExceptionShouldReturn500() {
        given()
                .queryParam("message", SAMPLE_DETAIL)
                .get("/throw/validation/constraint-declaration-exception")
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
                .body("title", equalTo(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("status", equalTo(INTERNAL_SERVER_ERROR.getStatusCode()))
                .body("detail", nullValue())
                .body("stacktrace", nullValue());
    }

    @Test
    void constraintViolationShouldProvideErrorDetails() {
        given()
                .body("{\"phraseName\": 10 }")
                .contentType(APPLICATION_JSON)
                .post("/throw/validation/constraint-violation-exception")
                .then()
                .statusCode(422)
                .body("title", equalTo("Unprocessable Entity"))
                .body("status", equalTo(422))
                .body("errors", hasSize(1))
                .body("errors[0].pointer", equalTo("phraseName"))
                .body("errors[0].detail", equalTo("must be greater than or equal to 15"))
                .body("stacktrace", nullValue());
    }

    @Test
    void constraintViolationForArgumentsShouldProvideErrorDetails() {
        given()
                .contentType(APPLICATION_JSON)
                .body("{\"phraseName\": 1}")
                .queryParam("param_name", "invalidQueryParam")
                .queryParam("param_name2", "validQueryParam")
                .header("param_name3", "invalidHeaderParam")
                .pathParam("param_name4", "invalidPathParam")
                .post("/throw/validation/constraint-violation-exception/{param_name4}")
                .then()
                .statusCode(422)
                .body("title", equalTo("Unprocessable Entity"))
                .body("status", equalTo(422))
                .body("errors", hasSize(4))
                .body("errors.find{it.in == 'query'}.pointer", equalTo("param_name"))
                .body("errors.find{it.in == 'query'}.detail", equalTo("length must be between 10 and 15"))
                .body("errors.find{it.in == 'header'}.pointer", equalTo("param_name3"))
                .body("errors.find{it.in == 'header'}.detail", equalTo("length must be between 10 and 15"))
                .body("errors.find{it.in == 'path'}.pointer", equalTo("param_name4"))
                .body("errors.find{it.in == 'path'}.detail", equalTo("length must be between 10 and 15"))
                .body("errors.find{it.in == 'body'}.pointer", equalTo("phraseName"))
                .body("errors.find{it.in == 'body'}.detail", equalTo("must be greater than or equal to 15"));
    }

}