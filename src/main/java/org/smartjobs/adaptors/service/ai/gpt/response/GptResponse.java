package org.smartjobs.adaptors.service.ai.gpt.response;

import org.smartjobs.adaptors.service.ai.gpt.data.GptModel;

import java.util.List;

public record GptResponse(String id, String object, long created, GptModel model, GptUsage usage,
                          List<GptChoices> choices) {
}
