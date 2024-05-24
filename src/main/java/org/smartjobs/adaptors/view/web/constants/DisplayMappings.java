package org.smartjobs.adaptors.view.web.constants;

import org.smartjobs.core.failures.ProcessFailure;

public class DisplayMappings {

    private DisplayMappings() {
        //Empty Constructor to prevent instantiation.
    }

    public static String mapProcessingFailure(ProcessFailure processFailure) {
        return switch (processFailure) {
            case LLM_FAILURE_UPLOADING -> "Candidate information could not be extracted.";
            case EXISTING_CANDIDATE -> "Candidate already applied to role.";
            case FAILURE_TO_READ_FILE -> "The uploaded file could not be read.";
            case LLM_FAILURE_ANALYZING -> "There was a failure when getting the analysis result.";
        };
    }

}
