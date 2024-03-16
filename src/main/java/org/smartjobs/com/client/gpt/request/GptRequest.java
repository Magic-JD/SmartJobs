package org.smartjobs.com.client.gpt.request;

import org.smartjobs.com.client.gpt.data.GptMessage;
import org.smartjobs.com.client.gpt.data.GptModel;

import java.util.Arrays;
import java.util.List;

public record GptRequest(GptModel model, List<GptMessage> messages) {
    public static GptRequest gpt3(GptMessage... messages) {
        return new GptRequest(GptModel.GPT_3_5, Arrays.stream(messages).toList());
    }
}