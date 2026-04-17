package org.jmhreif.domain;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.stream.Collectors;

@NodeEntity
public record Organization(@Id String id,
                           String name,
                           @Relationship(value = "MENTIONS", direction = Relationship.Direction.INCOMING) List<Article> articles,
                           @Relationship("HAS_CATEGORY") List<IndustryCategory> industries) {

    public String toContextString() {
        String industryList = (industries == null) ? "" :
                industries.stream().map(IndustryCategory::name).collect(Collectors.joining(", "));
        String articleList = (articles == null) ? "" :
                articles.stream()
                        .map(a -> String.format("'%s' (sentiment: %.2f)", a.title(),
                                a.sentiment() != null ? a.sentiment() : 0.0))
                        .collect(Collectors.joining("; "));
        return String.format("Organization: %s | Industries: %s | Articles: %s",
                name, industryList, articleList);
    }
}
