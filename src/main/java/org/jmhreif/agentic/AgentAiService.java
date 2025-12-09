package org.jmhreif.agentic;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(tools = RAGTools.class)
@SystemMessage("""
        You are useful assistant that calls tools to reply to questions.
        """)
public interface AgentAiService {

    String agenticChat(@UserMessage String question);
}
