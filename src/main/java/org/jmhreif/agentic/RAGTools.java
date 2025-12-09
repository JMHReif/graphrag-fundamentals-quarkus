package org.jmhreif.agentic;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jmhreif.OrganizationRepository;
import org.jmhreif.domain.Organization;

import java.util.List;
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

    @Tool("Answer questions about news article contents, topics, or sentiment")
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

    @Tool("Answer questions about organizations, industries, and cities in the graph")
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
}
