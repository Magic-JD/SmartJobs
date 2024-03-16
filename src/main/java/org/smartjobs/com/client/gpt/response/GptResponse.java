package org.smartjobs.com.client.gpt.response;

import org.smartjobs.com.client.gpt.data.GptModel;

import java.util.List;

public record GptResponse(String id, String object, long created, GptModel model, GptUsage usage,
                          List<GptChoices> choices) {
}
