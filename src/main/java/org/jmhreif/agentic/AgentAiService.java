package org.jmhreif.agentic;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(tools = RAGTools.class)
@SystemMessage("""
        You are useful assistant that calls tools to reply to questions.
        When using the neo4j_cypher tools, always call the get_neo4j_schema tool first and always return the executed Cypher query with the answer.
        """)
public interface AgentAiService {

    String agenticChat(@UserMessage String question);
}
