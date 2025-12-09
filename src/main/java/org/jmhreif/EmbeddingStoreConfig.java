package org.jmhreif;

import dev.langchain4j.community.store.embedding.neo4j.Neo4jEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

@ApplicationScoped
public class EmbeddingStoreConfig {

    @Produces
    @Named("vectorStore")
    @ApplicationScoped
    public EmbeddingStore<TextSegment> vectorStore() {
        Driver driver = GraphDatabase.driver(
            "neo4j+s://demo.neo4jlabs.com",
            AuthTokens.basic("companies", "companies")
        );

        return Neo4jEmbeddingStore.builder()
            .driver(driver)
            .dimension(1536)
            .databaseName("companies")
            .label("Chunk")
            .indexName("news")
            .initializeSchema(false)
            .build();
    }
}
