package se.kebr.resource;

import static org.junit.Assert.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import se.kebr.model.Status;
import se.kebr.model.Team;
import se.kebr.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserResourceTest {

	private String userUrl = "http://localhost:8080/users";
	private String teamUrl = "http://localhost:8080/teams";
	private Client client;
	private Response response;

	@Before
	public void startUp() {
		client = ClientBuilder.newClient();
	}

	@Test
	public void canCreateAndGetUser() {

		response = client.target(userUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new User("Kevin", "Briffa", "Kebr92", Status.ACTIVE)));

		String loc = response.getHeaderString("location");
		assertTrue(response.getStatus() == 201);

		User getResults = client.target(loc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(User.class);
		
		assertTrue(getResults != null);

	}

	@Test
	public void canAddAndGetUserInTeam() {
		response = client.target(userUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.entity(new User("Kevin", "Briffa", "Kebr912", Status.ACTIVE),
						MediaType.APPLICATION_JSON));

		String userLoc = response.getHeaderString("location");
		assertTrue(response.getStatus() == 201);

		response = client.target(teamUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.entity(new Team("TestTeam"),
						MediaType.APPLICATION_JSON));
		
		String teamLoc = response.getHeaderString("location");
		assertTrue(response.getStatus() == 201);

		User user = client.target(userLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(User.class);

		Team team = client.target(teamLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(Team.class);
		
		response = client.target(userUrl)
				.path("team")
				.path("/")
				.path(team.getTeamNumber())
				.path("/")
				.path(user.getUserNumber())
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.put(Entity.json(String.class));
	}
	
	@Test
	public void canUpdateUser(){
		
		response = client.target(userUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new User("Json", "jackson", "jsonJkson", Status.ACTIVE)));
		
		String loc = response.getHeaderString("location");
		
		User user = client.target(loc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(User.class);
				
		
		
		response = client.target(userUrl)
				.path(user.getUserNumber())
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.put(Entity.json(new User("Updated", "Updated","Updated",Status.ACTIVE)));
				
			assertTrue(response.getStatus() == 200);
				
	}

}
