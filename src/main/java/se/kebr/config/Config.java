package se.kebr.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import se.kebr.exceptions.WebExceptionMapper;
import se.kebr.filter.AuthFilter;
import se.kebr.resource.IssueResource;
import se.kebr.resource.TeamResource;
import se.kebr.resource.UserResource;
import se.kebr.resource.WorkItemResource;

@Configuration
public class Config extends ResourceConfig {

	public Config() {
		register(UserResource.class);
		register(TeamResource.class);
		register(WorkItemResource.class);
		register(IssueResource.class);
		register(WebExceptionMapper.class);
		register(AuthFilter.class);
	}
}
