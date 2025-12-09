package org.jmhreif;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(retrievalAugmentor = Retriever.class)
@SystemMessage("You are a news expert providing answers to questions about news articles, and the organizations mentioned in them.")
public interface OrganizationAiService {
    @UserMessage("""
            Based on this question:
            {question}
            """)
    String vectorSearch(String question);

    @UserMessage("""
            Based on this question:
            {question}
            
            PHRASE:
            {context}
            """)
    String hybridSearch(String context, String question);
}
