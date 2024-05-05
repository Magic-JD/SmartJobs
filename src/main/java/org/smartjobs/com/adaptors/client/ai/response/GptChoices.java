package org.smartjobs.com.adaptors.client.ai.response;

import org.smartjobs.com.adaptors.client.ai.data.GptMessage;

public record GptChoices(GptMessage message, String logprobs, String finishReason, int index) {
}
