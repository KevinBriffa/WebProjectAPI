package se.kebr.resource;

import java.net.URI;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import static javax.ws.rs.core.Response.Status.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.kebr.exceptions.WebServiceException;
import se.kebr.model.Team;
import se.kebr.model.User;
import se.kebr.service.EntityService;

@Component
@Path("/users")
public final class UserResource {

	@Context
	private UriInfo uriInfo;

	private EntityService service;

	@Autowired
	public UserResource(EntityService service) {
		this.service = service;
	}

	@POST
	public Response addUser(User user) {
		User dbUser = service.saveOrUpdateUser(user);
		URI location = uriInfo.getAbsolutePathBuilder().path(dbUser.getUserNumber()).build();
		return Response.created(location).build();
	}

	@GET
	@Path("{entityNumber}")
	public Response getUserByNumber(@PathParam("entityNumber") String entityNumber) {
		if (service.findUserByNumber(entityNumber) != null) {
			return Response.ok(service.findUserByNumber(entityNumber)).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	@GET
	@Path("all/{content}")
	public Response getUser(@PathParam("content") String content) {
		if (service.findUserByAll(content).size() != 0) {
			return Response.ok(service.findUserByAll(content)).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	@GET
	@Path("team/{entityNumber}")
	public Response getUsersInTeam(@PathParam("entityNumber") String number) {
		if (service.getAllUsersInATeam(number).size() != 0) {
			return Response.ok(service.getAllUsersInATeam(number)).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	@PUT
	@Path("{entityNumber}")
	public Response updateUser(@PathParam("entityNumber") String number, User user) {
		if (service.findUserByNumber(number).getId() != null) {
			return Response.ok(service.saveOrUpdateUser(updatedUser(number, user))).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	@PUT
	@Path("team/{teamNumber}/{userNumber}")
	public Response addUserToTeam(@PathParam("teamNumber") String teamNumber,
			@PathParam("userNumber") String userNumber) {
		Team team = service.findTeamByEntityNumber(teamNumber);
		User user = service.findUserByNumber(userNumber);

		if (team.getId() != null && user.getId() != null) {
			return Response.ok(service.addUserToTeam(team.getId(), user.getId())).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	private User updatedUser(String content, User user) {
		User dbUser = service.findUserByNumber(content);
		dbUser.setFirstname(user.getFirstname());
		dbUser.setLastname(user.getLastname());
		dbUser.setUsername(user.getUsername());
		dbUser.setStatus(user.getStatus());
		return dbUser;
	}

}
