package org.smartjobs.com.adaptors.client.ai.response;

import org.smartjobs.com.adaptors.client.ai.data.GptModel;

import java.util.List;

public record GptResponse(String id, String object, long created, GptModel model, GptUsage usage,
                          List<GptChoices> choices) {
}
