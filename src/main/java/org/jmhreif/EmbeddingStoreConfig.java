package org.jmhreif;

import dev.langchain4j.community.store.embedding.neo4j.Neo4jEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.neo4j.driver.Driver;

/**
 * Produces the Neo4j vector store explicitly, for one reason: to set initializeSchema(false).
 *
 * The quarkus-langchain4j-neo4j extension always initializes the schema (initializeSchema
 * defaults to true and the extension exposes no config key to disable it). Its createSchema()
 * step rejects the pre-existing `news_openai_small` index because that is a composite vector
 * index (properties: embedding_3_small, siteName, date, sentiment, language), not the
 * single-property index the store would create. We only read the existing index, so schema
 * creation must be skipped.
 *
 * Everything else stays in application.properties as the single source of truth: the connection
 * comes from the managed quarkus.neo4j.* Driver, and the store settings are read from the same
 * quarkus.langchain4j.neo4j.* keys the extension uses. This producer overrides the extension's
 * default EmbeddingStore bean.
 */
@ApplicationScoped
public class EmbeddingStoreConfig {

    @Produces
    @ApplicationScoped
    public EmbeddingStore<TextSegment> vectorStore(
            Driver driver,
            @ConfigProperty(name = "quarkus.langchain4j.neo4j.database-name") String databaseName,
            @ConfigProperty(name = "quarkus.langchain4j.neo4j.index-name") String indexName,
            @ConfigProperty(name = "quarkus.langchain4j.neo4j.label") String label,
            @ConfigProperty(name = "quarkus.langchain4j.neo4j.dimension") int dimension,
            @ConfigProperty(name = "quarkus.langchain4j.neo4j.embedding-property") String embeddingProperty) {
        return Neo4jEmbeddingStore.builder()
                .driver(driver)
                .databaseName(databaseName)
                .indexName(indexName)
                .label(label)
                .dimension(dimension)
                .embeddingProperty(embeddingProperty)
                .initializeSchema(false)
                .build();
    }
}
