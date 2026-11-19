package rocks.konrath.weatherhub;

import jakarta.ws.rs.container.ContainerRequestContext;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

import io.vertx.core.http.HttpServerRequest;

/** Logs incoming requests on DEBUG level. */
public class RequestLoggingFilter {

	private static final Logger LOG = Logger.getLogger(RequestLoggingFilter.class);

	@ServerRequestFilter
	public void logRequest(ContainerRequestContext context, HttpServerRequest request) {
		if (LOG.isDebugEnabled()) {
			LOG.debugf("Request %s %s from %s", context.getMethod(), context.getUriInfo().getPath(),
					request.remoteAddress());
		}
	}
}
