package se.kebr.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

public final class WebServiceException extends WebApplicationException {

	private static final long serialVersionUID = 759915140760342492L;

	public WebServiceException(Status status) {
		super(status);
	}

}
