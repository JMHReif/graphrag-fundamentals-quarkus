package org.jmhreif;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jmhreif.domain.Organization;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
@Path("/")
public class OrganizationResource {
    private final OrganizationRepository repository;
    private final OrganizationAiService organizationAiService;

    //Demo purpose
    @Inject
    EmbeddingModel embeddingModel;
    @Inject
    EmbeddingStore<TextSegment> embeddingStore;
    @Inject
    AiService aiService;

    public OrganizationResource(OrganizationRepository repository, OrganizationAiService aiService) {
        this.repository = repository;
        this.organizationAiService = aiService;
    }

    @Path("/articleMentions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Iterable<Organization> getArticleMentions() {
        return repository.findOrganizations();
    }

    @Path("/llm")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String askQuestion(@QueryParam("question") String question) {
        return aiService.chat(question);
    }

    @Path("/vectorRAG")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String manualVectorRAG(@QueryParam("question") String question) {
        return organizationAiService.vectorSearch(question);
    }

    @Path("/manualGraphRAG")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String manualGraphRAG(@QueryParam("question") String question) {
        // Embed the question and execute vector similarity search
        Embedding searchPhraseEmbedding = embeddingModel.embed(question).content();
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(searchPhraseEmbedding)
                .maxResults(4)
                .build();
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embeddingStore.search(embeddingSearchRequest).matches();
        // System.out.println("----- Sample similarity result -----");
        // System.out.println(relevantEmbeddings.get(0).toString());

        // Graph retrieval query (using vector search results)
        Iterable<Organization> orgList = repository.findOrganizationsByChunkIds(
                relevantEmbeddings.stream()
                        .map(o -> o.embeddingId())
                        .collect(java.util.stream.Collectors.toList())
        );
        // System.out.println("----- Org list -----");
        // System.out.println(orgList);

        // Format graph results for prompt
        String context = (orgList == null)
                ? ""
                : StreamSupport.stream(orgList.spliterator(), false)
                .map(Organization::toString)
                .collect(Collectors.joining("\n"));

        //Call the LLM with custom prompt and return answer text
        return organizationAiService.hybridSearch(context, question);
    }

    //Demo purpose
    @Path("/compare")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String vectorPlusComparison(@QueryParam("question") String question) {
        // Generate embedding for the question
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        // Small result set (4)
        EmbeddingSearchRequest smallRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(4)
                .build();
        List<EmbeddingMatch<TextSegment>> smallResults = embeddingStore.search(smallRequest).matches();

        List<String> smallCompanies = repository.findEntitiesCompare(
                smallResults.stream()
                        .map(match -> match.embeddingId())
                        .collect(Collectors.toList())
        );

        // Large result set (8)
        EmbeddingSearchRequest largeRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(8)
                .build();
        List<EmbeddingMatch<TextSegment>> largeResults = embeddingStore.search(largeRequest).matches();

        List<String> largeCompanies = repository.findEntitiesCompare(
                largeResults.stream()
                        .map(match -> match.embeddingId())
                        .collect(Collectors.toList())
        );

        return String.format("""
            QUESTION: %s

            SMALL_SET_COUNT: %d
            SMALL_SET_ORGS:
            %s

            LARGE_SET_COUNT: %d
            LARGE_SET_ORGS:
            %s
            """,
                question,
                smallCompanies.size(),
                String.join("\n", smallCompanies),
                largeCompanies.size(),
                String.join("\n", largeCompanies)
        );
    }
}
