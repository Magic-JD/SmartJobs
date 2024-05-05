package org.smartjobs.com.adaptors.client.ai.response;

public record GptUsage(int promptTokens, int completionTokens, int totalTokens) {
}
