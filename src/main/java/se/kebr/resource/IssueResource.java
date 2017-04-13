package se.kebr.resource;

import java.net.URI;
import java.util.Collection;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import static javax.ws.rs.core.Response.Status.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kebr.exceptions.WebServiceException;
import se.kebr.model.Issue;
import se.kebr.model.WorkItem;
import se.kebr.service.EntityService;

@Component
@Path("/issues")
public class IssueResource {

	@Context
	private UriInfo uriInfo;

	private EntityService service;

	@Autowired
	public IssueResource(EntityService service) {
		this.service = service;
	}

	@POST
	@Path("item/{itemNumber}")
	public Response assignIssue(@PathParam("itemNumber") String itemNumber, Issue issue) {
		WorkItem dbItem = service.getWorkItemByNumber(itemNumber);
		Issue dbIssue = service.saveOrUpdateIssue(issue, dbItem.getId());
		URI location = uriInfo.getAbsolutePathBuilder().path(dbIssue.getIssuenumber()).build();
		return Response.created(location).build();
	}

	@GET
	@Path("issuedItems")
	public Response getIssuedItems() {
		Collection<WorkItem> issuedItems = service.getIssuedWorkItems();
		if (issuedItems.size() != 0) {
			return Response.ok(issuedItems).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

}
