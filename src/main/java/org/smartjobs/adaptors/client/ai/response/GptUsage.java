package org.smartjobs.adaptors.client.ai.response;

public record GptUsage(int promptTokens, int completionTokens, int totalTokens) {
}
