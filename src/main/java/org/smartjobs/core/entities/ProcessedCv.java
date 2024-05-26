package org.smartjobs.core.entities;

public record ProcessedCv(Long id,
                          String name,
                          boolean currentlySelected,
                          String fileHash,
                          String condensedDescription) {
}
