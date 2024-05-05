package org.smartjobs.com.core.entities;

public record ProcessedCv(Long id, String name, boolean currentlySelected, String fileHash,
                          String condensedDescription) {
}
