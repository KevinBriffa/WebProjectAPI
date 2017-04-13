package se.kebr.resource;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import se.kebr.model.Team;


@RunWith(SpringRunner.class)
@SpringBootTest
public class TeamResourceTest {
	
	private String teamUrl = "http://localhost:8080/teams";
	private Client client;
	private Response response;

	@Before
	public void startUp() {
		client = ClientBuilder.newClient();
	}

	@Test
	public void canCreateAndGetTeam() {
		
		response = client.target(teamUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new Team("TeamResTest")));
		
		String teamLoc = response.getHeaderString("location");
		assertTrue(response.getStatus() == 201);
		
		Team team = client.target(teamLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(Team.class);
		
		assertTrue(team.getName().equals("TeamResTest"));
	}
	@Test
	public void canUpdateTeam(){
		
		response = client.target(teamUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new Team("TeamTestForUpdate")));
		
		String teamLoc = response.getHeaderString("location");
		
		assertTrue(response.getStatus() == 201);
		
		Team team = client.target(teamLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(Team.class);
		
		assertTrue(team.getName().equals("TeamTestForUpdate"));
		
		response = client.target(teamUrl)
				.path(team.getTeamNumber())
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.put(Entity.json(new Team("UPDATED")));
		
		assertTrue(response.getStatus() == 200);
		
	}
	@Test
	public void canGetAllTeams(){
		
		response = client.target(teamUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new Team("TestTeam")));
		
		
		List<Team> allTeams = client.target(teamUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(new GenericType<List<Team>>(){});
		
		assertTrue(allTeams.size() > 1);
		
	}
	

}
