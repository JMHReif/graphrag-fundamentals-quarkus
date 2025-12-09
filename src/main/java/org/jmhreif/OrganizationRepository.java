package org.jmhreif;

import jakarta.enterprise.context.ApplicationScoped;
import org.jmhreif.domain.Organization;
import org.neo4j.ogm.session.SessionFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class OrganizationRepository {
    private final SessionFactory sessionFactory;

    public OrganizationRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    Iterable<Organization> findOrganizations() {
        String cypherQuery = """
                MATCH (o:Organization)<-[rel:MENTIONS]-(a:Article)
                RETURN o, collect(rel), collect(a) LIMIT 3;
                """;
        return sessionFactory.openSession().query(Organization.class,
                cypherQuery, Collections.emptyMap());
    }

    public Iterable<Organization> findOrganizationsByChunkIds(List<String> chunkIds) {
        String cypherQuery = """
                MATCH (o:Organization)<-[rel:MENTIONS]-(a:Article)-[rel2:HAS_CHUNK]->(c:Chunk)
                WHERE c.id IN $chunkIds
                OPTIONAL MATCH (o)-[rel3:HAS_CATEGORY]->(i:IndustryCategory)
                RETURN o, collect(rel), collect(a), collect(rel2), collect(c), collect(rel3), collect(i);
                """;
        return sessionFactory.openSession().query(Organization.class,
                cypherQuery, Map.of("chunkIds", chunkIds));
    }

    //Demo purpose
    List<String> findEntitiesCompare(List<String> chunkIds) {
        String cypherQuery = """
                MATCH (o:Organization)<-[rel:MENTIONS]-(a:Article)-[rel2:HAS_CHUNK]->(c:Chunk)
                WHERE c.id IN $chunkIds
                RETURN DISTINCT o.name as name ORDER BY name DESC;
                """;
        Iterable<Map<String, Object>> iterable = sessionFactory.openSession().query(
                        cypherQuery, Map.of("chunkIds", chunkIds));

        return StreamSupport.stream(iterable.spliterator(), false)
                .map(row -> (String) row.get("name"))
                .collect(Collectors.toList());
    }
}
