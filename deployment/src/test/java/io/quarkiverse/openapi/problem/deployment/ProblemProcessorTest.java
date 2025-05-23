package io.quarkiverse.openapi.problem.deployment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.quarkus.deployment.Capabilities;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class ProblemProcessorTest {

    static final Capabilities CAPABILITIES_WITH_JSON = new Capabilities(Collections.singleton("io.quarkus.jackson"));
    static final Capabilities CAPABILITIES_WITHOUT_JSON = new Capabilities(Collections.singleton("io.quarkus.resteasy"));

    final Logger logger = mock(Logger.class);
    final ProblemProcessor problemProcessor = createProcessorWith(logger);

    /**
     * That's the only way I found to inject mock logger into ProblemProcessor. Quarkus forbids having multiple constructors in
     * deployment processors, or have fields that are not @BuildItem, which makes it impossible to inject anything via
     * constructor or even setter.
     */
    private ProblemProcessor createProcessorWith(Logger logger) {
        return new ProblemProcessor() {
            @Override
            protected Logger logger() {
                return logger;
            }
        };
    }

    @Test
    void featureNameShouldBeValid() {
        assertThat(problemProcessor.createFeature(CAPABILITIES_WITH_JSON).getName())
                .isEqualTo("openapi-problem");

        verify(logger, times(0)).error(anyString());
    }

    @Test
    void shouldLogErrorIfMissingJsonCapability() {
        problemProcessor.createFeature(CAPABILITIES_WITHOUT_JSON);

        verify(logger).error("`quarkus-openapi-problem` extension is useless without a REST Json Provider. "
                + "Please add `quarkus-rest-jackson` or `quarkus-rest-jsonb` (or reactive versions) to your pom.xml.");
    }

}
