package se.kebr.resource;

import static javax.ws.rs.core.Response.Status.*;
import java.net.URI;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import se.kebr.model.Status;
import se.kebr.model.Team;
import se.kebr.model.User;
import org.springframework.stereotype.Component;
import se.kebr.exceptions.WebServiceException;
import se.kebr.model.WorkItem;
import se.kebr.service.EntityService;

@Component
@Path("/workitems")
public class WorkItemResource {

	@Context
	private UriInfo uriInfo;

	private EntityService service;

	public WorkItemResource(EntityService service) {
		this.service = service;
	}

	@POST
	public Response createWorkItem(WorkItem item) {
		WorkItem dbItem = service.saveOrUpdateWorkItem(item);
		URI location = uriInfo.getAbsolutePathBuilder().path(dbItem.getItemnumber()).build();
		return Response.created(location).build();
	}

	@PUT
	@Path("{entityNumber}")
	public Response updateWorkItem(@PathParam("entityNumber") String entityNumber, WorkItem item) {
		if (service.getWorkItemByNumber(entityNumber) != null) {
			return Response.ok(service.saveOrUpdateWorkItem(updatedWorkItem(entityNumber, item))).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	@PUT
	@Path("user/{itemNumber}/{userNumber}")
	public Response assignItemToUser(@PathParam("itemNumber") String itemNumber,
			@PathParam("userNumber") String userNumber) {
		WorkItem dbItem = service.getWorkItemByNumber(itemNumber);
		User dbUser = service.findUserByNumber(userNumber);
		if (dbItem.getId() != null && dbUser.getId() != null) {
			service.assignWorkItem(dbItem.getId(), dbUser.getId());
			return Response.ok().build();
		}
		throw new WebServiceException(NOT_ACCEPTABLE);

	}

	@GET
	@Path("team/{teamNumber}")
	public Response getItemsByTeam(@PathParam("teamNumber") String teamNumber) {
		Team dbTeam = service.findTeamByEntityNumber(teamNumber);
		return Response.ok(service.getWorkItemsFromTeam(dbTeam.getId())).build();
	}

	@GET
	@Path("user/{userNumber}")
	public Response getItemsByUser(@PathParam("userNumber") String userNumber) {
		User dbUser = service.findUserByNumber(userNumber);
		return Response.ok(service.getWorkItemsByUser(dbUser.getId())).build();
	}

	@GET
	@Path("status/{status}")
	public Response getItemByStatus(@PathParam("status") Status status) {
		return Response.ok(service.getWorkItemsByStatus(status)).build();
	}

	@GET
	@Path("{entityNumber}")
	public Response getItemsByNumber(@PathParam("entityNumber") String entityNumber) {
		if (service.getWorkItemByNumber(entityNumber).getId() != null) {
			return Response.ok(service.getWorkItemByNumber(entityNumber)).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	@GET 
	@Path("description/{description}")
	public Response getItemByDescription(@PathParam("description") String description) {
		return Response.ok(service.getWorkItemByDescription(description)).build();
	}

	@DELETE
	@Path("{entityNumber}")
	public Response removeWorkItem(@PathParam("entityNumber") String entityNumber) {
		Long id = service.getWorkItemByNumber(entityNumber).getId();
		if (id != null) {
			service.deleteWorkItem(id);
			return Response.ok().build();
		}
		throw new WebServiceException(NOT_ACCEPTABLE);
	}

	private WorkItem updatedWorkItem(String number, WorkItem item) {
		WorkItem dbItem = service.getWorkItemByNumber(number);
		dbItem.setStatus(item.getStatus());
		dbItem.setName(item.getName());
		dbItem.setDescription(item.getDescription());
		return dbItem;
	}

}
