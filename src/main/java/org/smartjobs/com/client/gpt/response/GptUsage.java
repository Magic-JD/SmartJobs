package org.smartjobs.com.client.gpt.response;

public record GptUsage(int promptTokens, int completionTokens, int totalTokens) {
}
