package org.jmhreif.agentic;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.service.tool.ToolExecutionResult;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.quarkiverse.langchain4j.mcp.runtime.McpClientName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jmhreif.OrganizationRepository;
import org.jmhreif.domain.Organization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class RAGTools {

    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    EmbeddingStore<TextSegment> embeddingStore;

    @Inject
    OrganizationRepository repository;

    @Inject
    @McpClientName("neo4j-mcp")
    McpClient mcpClient;

    @Inject
    ObjectMapper objectMapper;

    @Tool("Semantic search over news article text. Use for content, topic, or sentiment questions (e.g. cybersecurity threats, market news, geopolitical events).")
    public String vectorSearch(String query) {
        // Generate embedding for the query
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        // Execute vector similarity search
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(4)
                .build();

        List<EmbeddingMatch<TextSegment>> results = embeddingStore.search(searchRequest).matches();

        String formattedResults = results.stream()
                .map(match -> String.format("Score: %.4f - %s",
                    match.score(),
                    match.embedded().text()))
                .collect(Collectors.joining("\n\n"));

        System.out.println("----- Vector Search Tool Results -----");
        System.out.println(formattedResults);

        return formattedResults;
    }

    @Tool("Semantic/trend questions about which organizations, industries, or cities appear in news topics — returns entities enriched with related articles.")
    public String graphEnrichedSearch(String query) {
        // Generate embedding for the query
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        // Execute vector similarity search
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(4)
                .build();

        List<EmbeddingMatch<TextSegment>> vectorResults = embeddingStore.search(searchRequest).matches();

        // Extract chunk IDs from vector search results
        List<String> chunkIds = vectorResults.stream()
                .map(EmbeddingMatch::embeddingId)
                .collect(Collectors.toList());

        // Retrieve graph-enriched results using chunk IDs
        Iterable<Organization> graphResults = repository.findOrganizationsByChunkIds(chunkIds);

        String formattedResults = StreamSupport.stream(graphResults.spliterator(), false)
                .map(Organization::toString)
                .collect(Collectors.joining("\n\n"));

        System.out.println("----- Graph Enriched Search Tool Results -----");
        System.out.println(formattedResults);

        return formattedResults;
    }

    @Tool("Get the Neo4j database schema including node labels, relationship types, and properties")
    public String getNeo4jSchema() {
        System.out.println("----- Getting Neo4j Schema -----");

        ToolExecutionRequest request = ToolExecutionRequest.builder()
                .name("get-schema")
                .arguments("{}")
                .build();

        ToolExecutionResult executionResult = mcpClient.executeTool(request);
        String result = executionResult.resultText();
        System.out.println("Schema: " + result);
        return result;
    }

    @Tool("Run read-only Cypher for EXACT lookups: specific named entities, numeric filters, counts, or aggregations. Call getNeo4jSchema first.")
    public String readCypher(String query) {
        System.out.println("----- Executing Cypher Query -----");
        System.out.println("Query: " + query);

        String arguments;
        try {
            arguments = objectMapper.writeValueAsString(Map.of("query", query));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize Cypher query arguments", e);
        }

        ToolExecutionRequest request = ToolExecutionRequest.builder()
                .name("read-cypher")
                .arguments(arguments)
                .build();

        ToolExecutionResult executionResult = mcpClient.executeTool(request);
        String result = executionResult.resultText();
        System.out.println("Result: " + result);
        // Return the executed query alongside the results so the model can include it in its answer.
        return String.format("Executed Cypher:%n%s%n%nResults:%n%s", query, result);
    }
}
