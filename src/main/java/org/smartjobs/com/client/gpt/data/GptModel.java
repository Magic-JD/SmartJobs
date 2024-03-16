package org.smartjobs.com.client.gpt.data;

import java.util.Arrays;

public enum GptModel {
    GPT_3_5("gpt-3.5-turbo"),
    UNKNOWN("unknown");

    private final String name;

    GptModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static GptModel fromName(String name) {
        return Arrays.stream(GptModel.values()).filter(gm -> gm.getName().equals(name)).findAny().orElse(UNKNOWN);
    }
}
