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
import org.springframework.stereotype.Component;
import se.kebr.exceptions.WebServiceException;
import se.kebr.model.Team;
import se.kebr.service.EntityService;

@Component
@Path("/teams")
public class TeamResource {

	@Context
	private UriInfo uriInfo;

	private EntityService service;

	public TeamResource(EntityService service) {
		this.service = service;
	}

	@POST
	public Response addTeam(Team team) {
		Team dbTeam = service.saveOrUpdateTeam(team);
		URI location = uriInfo.getAbsolutePathBuilder().path(dbTeam.getTeamNumber()).build();
		return Response.created(location).build();
	}

	@PUT
	@Path("{entityNumber}")
	public Response updateTeam(@PathParam("entityNumber") String entityNumber, Team team) {
		if (service.findTeamByEntityNumber(entityNumber) != null) {
			return Response.ok(service.saveOrUpdateTeam(updatedTeam(entityNumber, team))).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	@GET
	public Response getAllTeams() {
		if (service.getAllTeams().size() != 0) {
			return Response.ok(service.getAllTeams()).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	@GET
	@Path("{entityNumber}")
	public Response findTeamByNumber(@PathParam("entityNumber") String entityNumber) {
		if (service.findTeamByEntityNumber(entityNumber) != null) {
			return Response.ok(service.findTeamByEntityNumber(entityNumber)).build();
		}
		throw new WebServiceException(NOT_FOUND);
	}

	private Team updatedTeam(String number, Team team) {
		{
			Team dbTeam = service.findTeamByEntityNumber(number);
			dbTeam.setName(team.getName());
			return dbTeam;
		}

	}
}
