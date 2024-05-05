package org.smartjobs.adaptors.client.ai.data;

public record GptMessage(GptRole role, String content) {
    public static GptMessage systemMessage(String message) {
        return new GptMessage(GptRole.SYSTEM, message);
    }

    public static GptMessage userMessage(String message) {
        return new GptMessage(GptRole.USER, message);
    }
}
