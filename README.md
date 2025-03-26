
<div align="center">

<img src="https://github.com/quarkiverse/.github/blob/main/assets/images/quarkus.svg" width="67" height="70" ><img src="https://github.com/quarkiverse/.github/blob/main/assets/images/plus-sign.svg" height="70" ><img src="https://github.com/quarkiverse/quarkus-openapi-generator/blob/main/docs/modules/ROOT/assets/images/openapi.svg" height="70" >

# Quarkus - OpenAPI Problem (RFC-9457)
</div>
<br>

[![Version](https://img.shields.io/maven-central/v/com.melloware.openapi-problem/quarkus-openapi-problem?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/com.melloware.openapi-problem/quarkus-openapi-problem)
[![License](https://img.shields.io/badge/license-Apache%202.0-yellow.svg)](https://github.com/quarkiverse/quarkus-openapi-problem/blob/main/LICENSE.txt)
[![Build](https://github.com/melloware/quarkus-openapi-problem/actions/workflows/build.yml/badge.svg)](https://github.com/melloware/quarkus-openapi-problem/actions/workflows/build.yml)


[RFC9457 Problem](https://www.rfc-editor.org/rfc/rfc9457#problem-json) extension for Quarkus RESTeasy/JaxRS applications. It maps Exceptions to `application/problem+json` HTTP responses. Inspired by [Zalando Problem library](https://github.com/zalando/problem), originally open sourced by [Tietoevry](https://github.com/evry), now part of Quarkiverse.

This extension supports:
- Quarkus 3
- `quarkus-resteasy-jackson` and `quarkus-resteasy-jsonb`
- `quarkus-rest-jackson` and `quarkus-rest-jsonb`
- JVM and native mode

## Differences from Quarkus RestEASY Problem

This library extends the original [Quarkus RestEASY Problem](https://github.com/quarkiverse/quarkus-resteasy-problem) library by providing first-class OpenAPI support, making it seamless to integrate with OpenAPI specifications and tooling. Opinionated changes include the following:

- RFC-9457 support only (dropped support of RFC-7807)
- Validation constraint violations return `HTTP 422 Unprocessable Entity` status
- `HttpProblem` follows OpenAPI standards and is JSON serializable
- Mapped Diagnostic Context (MDC) values are included in the `context` field
- Validation errors are represented in the RFC9457 `errors` array
- `@ApiResponse` for 4xx/5xx automatically get mapped with `content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = HttpProblem.class))`
- Improve MicroProfile REST Client API to API forwarding of `HttpProblem` responses
- `.with(key, value)` is now `.withContext(key, value)`

## Why you should use this extension?
- __consistency__ - it unifies your REST API error messages, and gives it much needed consistency, no matter which JSON provider (Jackson vs JsonB) or paradigm (classic/blocking vs reactive) you're using.   

- __predictability__ - no matter what kind of exception is thrown: expected (thrown by you on purpose), or unexpected (not thrown 'by design') - your API consumer gets similar, repeatable experience.  

- __safety__ - it helps prevent leakage of some implementation details like stack-traces, DTO/resource class names etc.

- __time-saving__ - in most cases you will not have to implement your own JaxRS `ExceptionMapper`s anymore, which makes your app smaller, and less error-prone. 

See [Built-in Exception Mappers Wiki](https://github.com/quarkiverse/quarkus-openapi-problem/wiki#built-in-exception-mappers) for more details.

From [RFC9457](https://www.rfc-editor.org/rfc/rfc9457#name-introduction):
```
HTTP [RFC7230] status codes are sometimes not sufficient to convey
enough information about an error to be helpful.  While humans behind
Web browsers can be informed about the nature of the problem with an
HTML [W3C.REC-html5-20141028] response body, non-human consumers of
so-called "HTTP APIs" are usually not.
```

## Usage
### Quarkus 3.14+
Add this to your pom.xml:
```xml
<dependency>
    <groupId>com.melloware.openapi-problem</groupId>
    <artifactId>quarkus-openapi-problem</artifactId>
    <version>0.0.1</version>
</dependency>
```

Once you run Quarkus: `./mvnw compile quarkus:dev`, and you will find `openapi-problem` in the logs:
<pre>
Installed features: [cdi, rest, rest-jackson, <b><u>openapi-problem</u></b>]
</pre>

Now you can throw `HttpProblem`s (using builder or a subclass), JaxRS exceptions (e.g `NotFoundException`) or `ThrowableProblem`s from Zalando library:

```java
package problem;

import io.quarkiverse.openapi.problem.HttpProblem;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/hello")
public class HelloResource {

    @GET
    public String hello() {
        throw new HelloProblem("rfc9457-by-example");
    }

    static class HelloProblem extends HttpProblem {
        HelloProblem(String message) {
            super(builder()
                    .withTitle("Bad hello request")
                    .withStatus(Response.Status.BAD_REQUEST.getStatusCode())
                    .withDetail(message)
                    .withHeader("X-RFC9457-Message", message)
                    .withContext("hello", "world"));
        }
    }
}
```

Open [http://localhost:8080/hello](http://localhost:8080/hello) in your browser, and you should see this response:

```json
HTTP/1.1 400 Bad Request
X-RFC9457-Message: rfc9457-by-example
Content-Type: application/problem+json
        
{
    "status": 400,
    "title": "Bad hello request",
    "detail": "rfc9457-by-example",
    "instance": "/hello",
    "context": {
        "hello": "world"
    }
}
```

This extension will also produce the following log message:
```
10:53:48 INFO [http-problem] (executor-thread-1) status=400, title="Bad hello request", detail="rfc9457-by-example"
```
Exceptions transformed into http 500s (aka server errors) will be logged as `ERROR`, including full stacktrace.

You may also want to check [this article](https://dzone.com/articles/when-http-status-codes-are-not-enough-tackling-web) on RFC9457 practical usage.  
More on throwing problems: [zalando/problem usage](https://github.com/zalando/problem#usage)

## Configuration options

- (Build time) Include MDC properties in the API response. You have to provide those properties to MDC using `MDC.put`
```
quarkus.openapi.problem.include-mdc-properties=uuid,application,version
```
Result:
```json
{
  "status": 500,
  "title": "Internal Server Error",
  "context": {
    "uuid": "d79f8cfa-ef5b-4501-a2c4-8f537c08ec0c",
    "application": "awesome-microservice", 
    "version": "1.0"
  }
}
```

- (Runtime) Changes default `400 Bad request` response status when `ConstraintViolationException` is thrown (e.g. by Hibernate Validator)
```
quarkus.openapi.problem.constraint-violation.status=422
quarkus.openapi.problem.constraint-violation.title=Constraint violation
```
Result:
```json
HTTP/1.1 422 Unprocessable Entity
Content-Type: application/problem+json

{
    "status": 422,
    "title": "Constraint violation",
    (...)
}
```

- (Build time) Enable Smallrye (Microprofile) metrics for http error counters. Requires `quarkus-smallrye-metrics` in the classpath.

Please note that if you use `quarkus-micrometer-registry-prometheus` you don't need this feature - http error metrics will be produced regardless of this setting or presence of this extension.

```
quarkus.openapi.problem.metrics.enabled=true
```
Result:
```
GET /metrics
application_http_error_total{status="401"} 3.0
application_http_error_total{status="500"} 5.0
```

- (Runtime) Tuning logging
```
quarkus.log.category.http-problem.level=INFO # default: all problems are logged
quarkus.log.category.http-problem.level=ERROR # only HTTP 5XX problems are logged
quarkus.log.category.http-problem.level=OFF # disables all problems-related logging
```

## Custom ProblemPostProcessor
If you want to intercept, change or augment a mapped `HttpProblem` before it gets serialized into raw HTTP response 
body, you can create a bean extending `ProblemPostProcessor`, and override `apply` method.

Example:
```java
@ApplicationScoped
class CustomPostProcessor implements ProblemPostProcessor {
    
    @Inject // acts like normal bean, DI works fine etc
    Validator validator;
    
    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        return HttpProblem.builder(problem)
                .withContext("injected_from_custom_post_processor", "hello world " + context.path)
                .build();
    }
    
}
```

## Microprofile Client

When using MicroProfile REST Client to make HTTP calls between services, you need to register a `ResponseExceptionMapper` to properly handle and propagate `HttpProblem` responses. Otherwise, the original problem details will be lost when crossing service boundaries.

Add the following mapper to your codebase:

```java
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
```
