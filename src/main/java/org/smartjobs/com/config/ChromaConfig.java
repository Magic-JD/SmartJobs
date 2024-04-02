package org.smartjobs.com.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChromaConfig {


    public static EmbeddingStore<TextSegment> embeddingStore(String collectionName){
        return ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000/")
                .collectionName(collectionName)
                .build();
    }


    @Bean
    public EmbeddingModel embeddingModel(@Value("${gpt.api.key}") String apiKey){
        return OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("text-embedding-3-large")
                .build();
    }


}
