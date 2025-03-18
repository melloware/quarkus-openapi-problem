package io.quarkiverse.openapi.problem.deployment;

final class QuarkusSmallryeMetricsDetector extends ClasspathDetector {

    public QuarkusSmallryeMetricsDetector() {
        super("io.quarkus.smallrye.metrics.runtime.SmallRyeMetricsRecorder");
    }

}