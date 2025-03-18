package io.quarkiverse.openapi.problem.deployment;

final class JacksonDetector extends ClasspathDetector {

    JacksonDetector() {
        super("io.quarkus.jackson.ObjectMapperCustomizer");
    }

}