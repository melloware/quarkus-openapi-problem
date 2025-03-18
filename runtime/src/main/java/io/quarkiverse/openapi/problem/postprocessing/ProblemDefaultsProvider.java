package io.quarkiverse.openapi.problem.postprocessing;

import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkiverse.openapi.problem.InstanceUtils;

/**
 * Replaces <code>null</code> value of <code>instance</code> with URI of currently served endpoint, i.e
 * <code>/products/123</code>
 */
final class ProblemDefaultsProvider implements ProblemPostProcessor {

    @Override
    public int priority() {
        return 99;
    }

    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        if (problem.getInstance() != null) {
            return problem;
        }

        return problem.toBuilder()
                .withInstance(InstanceUtils.pathToInstance(context.path))
                .build();
    }

}