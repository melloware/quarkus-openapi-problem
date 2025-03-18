package io.quarkiverse.openapi.problem.postprocessing;

import static io.quarkiverse.openapi.problem.HttpProblemMother.badRequestProblem;
import static io.quarkiverse.openapi.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import io.quarkiverse.openapi.problem.HttpProblem;

class MicroprofileMetricsCollectorTest {

    ProblemPostProcessor processor = new MicroprofileMetricsCollector();

    @Test
    void shouldNotChangeProblemBuilder() {
        HttpProblem originalProblem = badRequestProblem();

        HttpProblem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem).isEqualTo(originalProblem);
    }

}