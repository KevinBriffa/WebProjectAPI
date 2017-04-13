package se.kebr.resource;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.print.CancelablePrintJob;
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
import se.kebr.model.Status;
import se.kebr.model.User;
import se.kebr.model.WorkItem;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class WorkItemResourceTest {

	private String itemUrl = "http://localhost:8080/workitems";
	private Client client;
	private Response response;


	@Before
	public void startUp() {
		client = ClientBuilder.newClient();
	}
	
	@Test
	public void canCreateAndGetWorkItem() {
		response = client.target(itemUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new WorkItem("ItemTest", "DescTest",Status.UNSTARTED)));

		String loc = response.getHeaderString("location");
		assertTrue(response.getStatus() == 201);

		WorkItem getResults = client.target(loc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(WorkItem.class);
		
		assertTrue(getResults != null);

	}
	
	@Test
	public void CanAssignAndGetAssignedItems() {
		
		response = client.target(itemUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new WorkItem("ItemTest2", "DescTest2",Status.UNSTARTED)));

		String itemLoc = response.getHeaderString("location");
		assertTrue(response.getStatus() == 201);

		response = client.target("http://localhost:8080/users")
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new User("ItemTestUser", "ItemtestLastname", "ItemTestUsrName", Status.ACTIVE)));
		
		String userLoc = response.getHeaderString("location");

		WorkItem item = client.target(itemLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(WorkItem.class);

		User user = client.target(userLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(User.class);
		
		response = client.target(itemUrl)
				.path("user")
				.path("/")
				.path(item.getItemnumber())
				.path("/")
				.path(user.getUserNumber())
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.put(Entity.json(String.class));
		
		
	}
	
	@Test
	public void canUpdateItem(){
		response = client.target(itemUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(new WorkItem("UpdateItem", "UpdateDesc",Status.UNSTARTED)));
		
		String itemLoc = response.getHeaderString("location");
		
		assertTrue(response.getStatus() == 201);
		
		WorkItem item = client.target(itemLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(WorkItem.class);
		
		assertTrue(item.getName().equals("UpdateItem"));
		
		response = client.target(itemUrl)
				.path(item.getItemnumber())
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.put(Entity.json(new WorkItem("UPDATED","UPDATED",Status.UNSTARTED)));
		
		assertTrue(response.getStatus() == 200);
		
	}
	
	@Test
	public void canGetItemByStatus(){
		
		
		WorkItem item = new WorkItem("ItemTestByStatus", "DescByStatus", Status.DONE);
		WorkItem secondItem = new WorkItem("ItemTestByStatus2", "DescByStatus2", Status.STARTED);
		WorkItem thirdItem = new WorkItem("ItemTestByStatus3", "DescByStatus3", Status.DONE);
		
		List<WorkItem> items = new ArrayList<>();
		items.add(item);
		items.add(secondItem);
		items.add(thirdItem);
		
		response = client.target(itemUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(items));
		
		List<WorkItem> itemsByStatus = client.target(itemUrl)
				.path("status")
				.path("DONE")
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(new GenericType<List<WorkItem>>(){});
		
		for(WorkItem currentItem : itemsByStatus){
			assertTrue(currentItem.getStatus() == Status.DONE);
		}
		
		
	}
	
	@Test
	public void canGetItemByDescription(){
		WorkItem item = new WorkItem("ItemDescritpionTest", "ItemByDescriptionTest", Status.DONE);
		
		response = client.target(itemUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(item));
		
		
		List<WorkItem> itemsByDescription = client.target(itemUrl)
				.path("description")
				.path(item.getDescription())
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(new GenericType<List<WorkItem>>(){});
		
		assertTrue(itemsByDescription.size() == 1);
		
	}
	
	@Test
	public void canDeleteWorkItem(){

		WorkItem item = new WorkItem("deletedItem", "deletedDesc", Status.DONE);
		
		response = client.target(itemUrl)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.post(Entity.json(item));
		
		assertTrue(response.getStatus() == 201);
		
		String itemLoc = response.getHeaderString("location");
		
		WorkItem deletedItem = client.target(itemLoc)
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.get(WorkItem.class);
		
		response = client.target(itemUrl)
				.path(deletedItem.getItemnumber())
				.request(MediaType.APPLICATION_JSON)
				.header("username", "admin")
				.header("password", "password")
				.delete();
		
		assertTrue(response.getStatus() == 200);
				
		
	}

}
