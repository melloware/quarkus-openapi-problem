package io.quarkiverse.openapi.problem.it;

import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkiverse.openapi.problem.postprocessing.ProblemContext;
import io.quarkiverse.openapi.problem.postprocessing.ProblemPostProcessor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class CustomPostProcessor implements ProblemPostProcessor {

    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        return problem.toBuilder()
                .withContext("injected_from_custom_post_processor", "you called " + context.path)
                .build();
    }
}