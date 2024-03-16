package org.smartjobs.com.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AddCandidateRequest(@NotNull @NotEmpty String candidateName, @NotNull @NotEmpty String cvData) {
}
