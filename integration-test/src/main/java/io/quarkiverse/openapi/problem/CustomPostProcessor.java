package io.quarkiverse.openapi.problem;

import io.quarkiverse.openapi.problem.postprocessing.ProblemContext;
import io.quarkiverse.openapi.problem.postprocessing.ProblemPostProcessor;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
class CustomPostProcessor implements ProblemPostProcessor {

    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        return HttpProblem.toBuilder(problem)
                .withContext("injected_from_custom_post_processor", "you called " + context.path)
                .build();
    }
}