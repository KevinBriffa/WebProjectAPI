package se.kebr.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthFilter implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequest) throws IOException {
		String username = containerRequest.getHeaderString("username");
		String password = containerRequest.getHeaderString("password");

		if (!("admin".equals(username) && "password".equals(password))) {
			containerRequest.abortWith(Response.status(Status.UNAUTHORIZED).build());
		}

	}

}
