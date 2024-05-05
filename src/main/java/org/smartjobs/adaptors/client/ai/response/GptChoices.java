package org.smartjobs.adaptors.client.ai.response;

import org.smartjobs.adaptors.client.ai.data.GptMessage;

public record GptChoices(GptMessage message, String logprobs, String finishReason, int index) {
}
