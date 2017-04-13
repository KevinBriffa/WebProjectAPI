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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import se.kebr.model.Issue;
import se.kebr.model.Status;
import se.kebr.model.WorkItem;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class IssueResourceTest {

	private String issueUrl = "http://localhost:8080/issues";
	private Client client;
	private Response response;

	@Before
	public void startUp() {
		client = ClientBuilder.newClient();
	}

	@Test
	public void canCreateAndAssignIssue() {

		response = client.target("http://localhost:8080/workitems")
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new WorkItem("IssueItemTest", "IssueItemDescTest", Status.DONE)));

		assertTrue(response.getStatus() == 201);
		
		String itemLoc = response.getHeaderString("location");

		WorkItem item = client.target(itemLoc).request(MediaType.APPLICATION_JSON).header("username", "admin")
				.header("password", "password").get(WorkItem.class);

		Issue issue = new Issue("IssueTest", "IssueDesc");

		response = client.target(issueUrl).path("item").path(item.getItemnumber()).request(MediaType.APPLICATION_JSON)
				.header("username", "admin").header("password", "password").post(Entity.json(issue));

	}

	@Test
	public void getIssuedWorkItems(){
		response = client.target("http://localhost:8080/workitems")
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new WorkItem("IssueItemTest", "IssueItemDescTest", Status.DONE)));
		
		String itemLoc = response.getHeaderString("location");
		
		response = client.target("http://localhost:8080/workitems")
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new WorkItem("IssueItemTest2", "IssueItemDescTest2", Status.DONE)));
		
		String secondItemLoc = response.getHeaderString("location");
		
		WorkItem item = client.target(itemLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(WorkItem.class);
		
		WorkItem secondItem = client.target(secondItemLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(WorkItem.class);
		
		response = client.target(issueUrl)
				.path("item")
				.path(item.getItemnumber())
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new Issue("IssueTest", "DescTest")));

		response = client.target(issueUrl)
				.path("item")
				.path(secondItem.getItemnumber())
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new Issue("IssueTest2", "DescTest2")));
	
		List<WorkItem> issuedItems = client.target(issueUrl)
				.path("issuedItems")
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(new GenericType<List<WorkItem>>(){});
	
		assertTrue(issuedItems.size() == 3);
		
	
	}

}
