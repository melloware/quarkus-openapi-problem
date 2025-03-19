package io.quarkiverse.openapi.problem.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;

@Singleton
public final class JacksonProblemModuleRegistrar implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule("RFC9457 problem");
        module.addSerializer(HttpProblem.class, new JacksonProblemSerializer());
        mapper.registerModule(module);
    }

}
