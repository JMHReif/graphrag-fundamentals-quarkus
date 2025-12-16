package org.jmhreif.agentic;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.McpClient;
import io.quarkiverse.langchain4j.mcp.runtime.McpClientName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/agents")
public class AgentResource {
    private final AgentAiService agentAiService;

    @Inject
    @McpClientName("sec-neo4j")
    McpClient mcpClient;

    public AgentResource(AgentAiService agentAiService) {
        this.agentAiService = agentAiService;
    }

    @Path("/debug/tools")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String debugTools() {
        List<ToolSpecification> tools = mcpClient.listTools();

        String toolsList = tools.stream()
                .map(tool -> String.format("- %s",
                        tool.name()))
                .collect(Collectors.joining("\n"));

        return String.format("""
                    MCP Tools Available (%d):
                    %s
                    """, tools.size(), toolsList);
    }

    @Path("/agentic")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String agenticChat(@QueryParam("question") String question) {
        return agentAiService.agenticChat(question);
    }
}
