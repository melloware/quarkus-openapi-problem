
<div align="center">

<img src="https://github.com/quarkiverse/.github/blob/main/assets/images/quarkus.svg" width="67" height="70" ><img src="https://github.com/quarkiverse/.github/blob/main/assets/images/plus-sign.svg" height="70" ><img src="https://github.com/quarkiverse/quarkus-openapi-generator/blob/main/docs/modules/ROOT/assets/images/openapi.svg" height="70" >

# Quarkus - OpenAPI Problem (RFC-9457)
</div>
<br>

> [!IMPORTANT]
> This extension has been archived because all of the changes here have been backported into Quarkus RestEasy Problem extension!

[![Version](https://img.shields.io/maven-central/v/com.melloware.openapi-problem/quarkus-openapi-problem?logo=apache-maven&style=flat-square)](https://search.maven.org/artifact/com.melloware.openapi-problem/quarkus-openapi-problem)
[![License](https://img.shields.io/badge/license-Apache%202.0-yellow.svg)](https://github.com/quarkiverse/quarkus-openapi-problem/blob/main/LICENSE.txt)
[![Build](https://github.com/melloware/quarkus-openapi-problem/actions/workflows/build.yml/badge.svg)](https://github.com/melloware/quarkus-openapi-problem/actions/workflows/build.yml)


[RFC9457 Problem](https://www.rfc-editor.org/rfc/rfc9457#problem-json) extension for Quarkus RESTeasy/JaxRS applications. It maps Exceptions to `application/problem+json` HTTP responses. Inspired by [Zalando Problem library](https://github.com/zalando/problem), originally open sourced by [Tietoevry](https://github.com/evry), now part of Quarkiverse.

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

