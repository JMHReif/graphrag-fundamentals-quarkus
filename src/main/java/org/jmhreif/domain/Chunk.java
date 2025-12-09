package org.jmhreif.domain;

import org.neo4j.ogm.annotation.Id;

public record Chunk(@Id String id,
                    String text) {
}
