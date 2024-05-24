package org.smartjobs.core.failures;

public enum ProcessFailure {
    EXISTING_CANDIDATE,
    LLM_FAILURE_UPLOADING,
    LLM_FAILURE_ANALYZING,
    FAILURE_TO_READ_FILE
}
