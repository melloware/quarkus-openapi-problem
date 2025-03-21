package io.quarkiverse.openapi.problem.openapi;

import io.quarkiverse.openapi.problem.HttpProblem;
import io.quarkus.smallrye.openapi.OpenApiFilter;
import io.smallrye.openapi.internal.models.media.Content;
import io.smallrye.openapi.internal.models.media.MediaType;
import io.smallrye.openapi.internal.models.media.Schema;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

/**
 * OpenAPI filter that automatically adds Problem Details schema to error responses.
 * This filter runs at build time and enhances the OpenAPI documentation by adding
 * the HttpProblem schema reference to any 4xx or 5xx response that doesn't already
 * have content defined.
 */
@OpenApiFilter(OpenApiFilter.RunStage.BUILD)
public class OpenApiProblemFilter implements OASFilter {

    private static final Content PROBLEM_CONTENT = OpenApiProblemFilter.createDefaultContent();

    /**
     * Filters API responses to add Problem Details schema for error responses.
     *
     * @param apiResponse The API response to filter
     * @return The filtered API response
     */
    @Override
    public APIResponse filterAPIResponse(APIResponse apiResponse) {
        if (apiResponse == null || apiResponse.getRef() != null) {
            return apiResponse;
        }

        if (!(apiResponse instanceof io.smallrye.openapi.internal.models.responses.APIResponse internalResponse)) {
            return apiResponse;
        }

        String responseCode = (String) internalResponse.getExtension("x-smallrye-private-response-code");
        if (responseCode == null || responseCode.isEmpty()) {
            return apiResponse;
        }

        try {
            int httpStatus = Integer.parseInt(responseCode);
            // Add Problem Details schema for 4xx and 5xx responses that don't have content
            if (httpStatus >= 400 && apiResponse.getContent() == null) {
                apiResponse.setContent(PROBLEM_CONTENT);
            }
        } catch (NumberFormatException e) {
            // Invalid response code, return unchanged
            return apiResponse;
        }

        return apiResponse;
    }

    /**
     * Creates the default content for Problem Details responses.
     * Sets up the media type and schema reference for HttpProblem.
     *
     * @return Content object configured for Problem Details
     */
    private static Content createDefaultContent() {
        Content content = new Content();

        MediaType mediaType = new MediaType();
        content.addMediaType(HttpProblem.APPLICATION_JSON_PROBLEM, mediaType);

        Schema schema = new Schema();
        schema.setRef("#/components/schemas/HttpProblem");
        mediaType.setSchema(schema);

        return content;
    }
}
