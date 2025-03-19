package io.quarkiverse.openapi.problem.postprocessing;

import static io.quarkiverse.openapi.problem.HttpProblemMother.badRequestProblem;
import static io.quarkiverse.openapi.problem.postprocessing.ProblemContextMother.simpleContext;
import static org.assertj.core.api.Assertions.assertThat;

import io.quarkiverse.openapi.problem.HttpProblem;
import org.junit.jupiter.api.Test;

class MicroprofileMetricsCollectorTest {

    ProblemPostProcessor processor = new MicroprofileMetricsCollector();

    @Test
    void shouldNotChangeProblemBuilder() {
        HttpProblem originalProblem = badRequestProblem();

        HttpProblem enhancedProblem = processor.apply(originalProblem, simpleContext());

        assertThat(enhancedProblem).isEqualTo(originalProblem);
    }

}
