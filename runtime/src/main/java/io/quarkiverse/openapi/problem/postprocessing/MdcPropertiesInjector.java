package io.quarkiverse.openapi.problem.postprocessing;

import io.quarkiverse.openapi.problem.HttpProblem;
import java.util.Collections;
import java.util.Set;
import org.slf4j.MDC;

/**
 * Injects existing MDC properties listed in the configuration into final response. Missing MDC values and properties already
 * defined in Problem instance are skipped.
 */
final class MdcPropertiesInjector implements ProblemPostProcessor {

    private final Set<String> properties;

    public MdcPropertiesInjector(Set<String> properties) {
        this.properties = Collections.unmodifiableSet(properties);
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public HttpProblem apply(HttpProblem problem, ProblemContext context) {
        if (properties.isEmpty()) {
            return problem;
        }

        HttpProblem.HttpProblemBuilder builder = problem.toBuilder();

        properties.stream()
                .filter(propertyName -> !problem.getContexts().containsKey(propertyName))
                .filter(propertyName -> MDC.get(propertyName) != null)
                .forEach(propertyName -> builder.withContext(propertyName, MDC.get(propertyName)));

        return builder.build();
    }

}
