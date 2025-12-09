package org.jmhreif;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface AiService {
    @UserMessage("{question}")
    String chat(String question);
}
