package org.smartjobs.com.service.candidate.data;

public record ProcessedCv(Long id, String name, boolean currentlySelected, String fileHash,
                          String condensedDescription) {
}
