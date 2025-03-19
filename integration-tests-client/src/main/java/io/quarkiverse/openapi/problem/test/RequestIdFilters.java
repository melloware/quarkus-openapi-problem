package io.quarkiverse.openapi.problem.test;

import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import java.util.UUID;
import org.jboss.logmanager.MDC;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

/**
 * Utility class for managing request-related information using JBossLog MDC
 * (Mapped Diagnostic Context). This class provides filters to automatically add
 * request information such as request ID and IP address to the MDC context,
 * making it available for logging purposes throughout the request lifecycle.
 */
public class RequestIdFilters {

    /**
     * Key for request ID stored in the MDC context.
     */
    public static final String REQUEST_ID_MDC_KEY = "X-Request-Id";

    /**
     * Key for request IP address stored in the MDC context.
     */
    public static final String REQUEST_IP_MDC_KEY = "X-Request-Address";


    /**
     * Injects the Vert.x RoutingContext to access the request object.
     */
    @Inject
    RoutingContext request;

    /**
     * Filter method executed before processing the incoming request. Adds
     * request ID and IP address to the MDC context.
     *
     * @param crc ContainerRequestContext provided by JAX-RS runtime.
     */
    @ServerRequestFilter
    public void addRequestInformation(ContainerRequestContext crc) {

        // Generate a unique request ID
        String requestId = UUID.randomUUID().toString();

        // Retrieve IP address from the incoming request
        String ipAddress = request.request().remoteAddress().hostAddress();

        // Add request ID and IP address to the MDC context
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        MDC.put(REQUEST_IP_MDC_KEY, ipAddress);

    }

    /**
     * Filter method executed after processing the incoming request. Clears
     * request-related information from the MDC context.
     */
    @ServerResponseFilter
    public void clearRequestInformation() {

        // Clear MDC context to avoid memory leaks
        MDC.clear();
    }
}
