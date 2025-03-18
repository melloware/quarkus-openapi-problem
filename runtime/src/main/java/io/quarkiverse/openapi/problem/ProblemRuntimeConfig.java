package io.quarkiverse.openapi.problem;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "quarkus.openapi.problem")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface ProblemRuntimeConfig {

    /**
     * Config for ConstraintViolationException mapper
     */
    @WithName("constraint-violation")
    ConstraintViolationMapperConfig constraintViolation();

    interface ConstraintViolationMapperConfig {
        static ConstraintViolationMapperConfig defaults() {
            return new ConstraintViolationMapperConfig() {
                @Override
                public int status() {
                    return 422;
                }

                @Override
                public String title() {
                    return "Unprocessable Entity";
                }
            };
        }

        /**
         * Response status code when ConstraintViolationException is thrown.
         */
        @WithDefault("422")
        int status();

        /**
         * Response title when ConstraintViolationException is thrown.
         */
        @WithDefault("Unprocessable Entity")
        String title();
    }
}