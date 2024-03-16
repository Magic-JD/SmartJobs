package org.smartjobs.com.client.gpt.response;

import org.smartjobs.com.client.gpt.data.GptMessage;

public record GptChoices(GptMessage message, String logprobs, String finishReason, int index) {
}
