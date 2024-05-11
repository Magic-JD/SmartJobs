package org.smartjobs.adaptors.service.ai.gpt;

import org.smartjobs.adaptors.service.ai.gpt.request.GptRequest;
import org.smartjobs.adaptors.service.ai.gpt.response.GptResponse;

import java.util.Optional;

public interface GptClient {

    Optional<GptResponse> makeServiceCall(GptRequest request);
}
