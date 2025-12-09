package org.jmhreif.domain;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;

@NodeEntity
public record Organization(@Id String id,
                           String name,
                           @Relationship(value = "MENTIONS", direction = Relationship.Direction.INCOMING) List<Article> articles,
                           @Relationship("HAS_CATEGORY") List<IndustryCategory> industries) {
}
