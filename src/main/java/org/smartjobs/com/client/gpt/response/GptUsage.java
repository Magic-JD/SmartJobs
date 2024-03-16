package org.smartjobs.com.client.gpt.response;

public record GptUsage(int prompt_tokens, int completion_tokens, int total_tokens) {
}
