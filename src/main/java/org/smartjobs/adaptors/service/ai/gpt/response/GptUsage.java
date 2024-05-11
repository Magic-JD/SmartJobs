package org.smartjobs.adaptors.service.ai.gpt.response;

public record GptUsage(int promptTokens, int completionTokens, int totalTokens) {
}
