package io.quarkiverse.openapi.problem.deployment;

final class JsonBDetector extends ClasspathDetector {

    JsonBDetector() {
        super("io.quarkus.jsonb.JsonbProducer");
    }

}