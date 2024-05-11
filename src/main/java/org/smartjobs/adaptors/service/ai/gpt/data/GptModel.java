package org.smartjobs.adaptors.service.ai.gpt.data;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GptModel {
    GPT_3_5("gpt-3.5-turbo"),
    GPT_4("gpt-4"),
    GPT_4_TURBO("gpt-4-turbo-preview"),
    UNKNOWN("unknown");

    private final String name;

    GptModel(String name) {
        this.name = name;
    }

    public static GptModel fromName(String name) {
        return Arrays.stream(GptModel.values()).filter(gm -> gm.getName().equals(name)).findAny().orElse(UNKNOWN);
    }
}
