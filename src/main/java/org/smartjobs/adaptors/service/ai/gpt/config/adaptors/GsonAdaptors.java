package org.smartjobs.adaptors.service.ai.gpt.config.adaptors;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.smartjobs.adaptors.service.ai.gpt.data.GptModel;
import org.smartjobs.adaptors.service.ai.gpt.data.GptRole;

import java.io.IOException;

public class GsonAdaptors {

    private GsonAdaptors() {
        // Private constructor to prevent instantiation.
    }

    public static class GptModelTypeAdapter extends TypeAdapter<GptModel> {
        @Override
        public void write(JsonWriter out, GptModel value) throws IOException {
            out.value(value.getName());
        }

        @Override
        public GptModel read(JsonReader in) throws IOException {
            String value = in.nextString();
            return GptModel.fromName(value);
        }
    }

    public static class GptRoleTypeAdapter extends TypeAdapter<GptRole> {
        @Override
        public void write(JsonWriter out, GptRole value) throws IOException {
            out.value(value.name().toLowerCase());
        }

        @Override
        public GptRole read(JsonReader in) throws IOException {
            String value = in.nextString();
            return GptRole.valueOf(value.toUpperCase());
        }
    }

}
