package org.jmhreif.domain;

import org.neo4j.ogm.annotation.Id;

public record IndustryCategory(@Id String id,
                               String name) {
}
