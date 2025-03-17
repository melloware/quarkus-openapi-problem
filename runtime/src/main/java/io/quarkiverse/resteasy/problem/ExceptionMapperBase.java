package io.quarkiverse.resteasy.problem;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import io.quarkiverse.resteasy.problem.postprocessing.PostProcessorsRegistry;
import io.quarkiverse.resteasy.problem.postprocessing.ProblemContext;

/**
 * Base class for all ExceptionMappers in this extension, takes care of mapping Exceptions to Problems, triggering
 * post-processing stage, and creating final JaxRS Response.
 */
public abstract class ExceptionMapperBase<E extends Throwable> implements ExceptionMapper<E> {

    public static final PostProcessorsRegistry postProcessorsRegistry = new PostProcessorsRegistry();

    @Context
    UriInfo uriInfo;

    @APIResponse(responseCode = "500", description = "Unexpected Error", content = @Content(mediaType = HttpProblem.APPLICATION_JSON_PROBLEM, schema = @Schema(implementation = HttpProblem.class)))
    @Override
    public final Response toResponse(E exception) {
        HttpProblem problem = toProblem(exception);
        ProblemContext context = ProblemContext.of(exception, uriInfo);
        HttpProblem finalProblem = postProcessorsRegistry.applyPostProcessing(problem, context);
        return finalProblem.toResponse();
    }

    protected abstract HttpProblem toProblem(E exception);

}
