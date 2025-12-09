package org.jmhreif.domain;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Relationship;

import java.time.ZonedDateTime;
import java.util.List;

public record Article(@Id String id,
                      String title,
                      String author,
                      String siteName,
                      ZonedDateTime date,
                      Double sentiment,
                      @Relationship("HAS_CHUNK") List<Chunk> chunks) {
}
