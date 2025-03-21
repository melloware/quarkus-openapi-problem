package io.quarkiverse.openapi.problem.security;

import io.quarkiverse.openapi.problem.ExceptionMapperBase;
import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkiverse.openapi.problem.postprocessing.ProblemContext;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 * Mapper overriding default Quarkus exception mapper to make all error responses compliant with RFC7807.
 *
 * @see io.quarkus.resteasy.runtime.UnauthorizedExceptionMapper
 */
public final class UnauthorizedExceptionReactiveMapper {

    @ServerExceptionMapper(value = UnauthorizedException.class, priority = Priorities.USER - 1)
    public Uni<Response> handle(RoutingContext routingContext, UnauthorizedException exception) {
        return HttpUnauthorizedUtils.toProblem(routingContext, exception)
                .map(problem -> {
                    ProblemContext context = ProblemContext.of(exception, routingContext.normalizedPath());
                    HttpProblem finalProblem = ExceptionMapperBase.postProcessorsRegistry.applyPostProcessing(problem,
                            context);
                    return finalProblem.toResponse();
                });
    }

}
