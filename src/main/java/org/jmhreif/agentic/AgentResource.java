package org.jmhreif.agentic;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/agents")
public class AgentResource {
    private final AgentAiService agentAiService;

    public AgentResource(AgentAiService agentAiService) {
        this.agentAiService = agentAiService;
    }

    @Path("/agentic")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String agenticChat(@QueryParam("question") String question) {
        return agentAiService.agenticChat(question);
    }
}
