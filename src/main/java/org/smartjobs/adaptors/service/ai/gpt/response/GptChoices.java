package org.smartjobs.adaptors.service.ai.gpt.response;

import org.smartjobs.adaptors.service.ai.gpt.data.GptMessage;

public record GptChoices(GptMessage message, String logprobs, String finishReason, int index) {
}
