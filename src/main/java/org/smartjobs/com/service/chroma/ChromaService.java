package org.smartjobs.com.service.chroma;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import org.smartjobs.com.config.ChromaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

@Service
public class ChromaService {

    private final EmbeddingModel embeddingModel;

    @Autowired
    public ChromaService(EmbeddingModel embeddingModel){
        this.embeddingModel = embeddingModel;
    }


    private void addDocuments(long storeId, List<TextSegment> segments) {
        List<Embedding> embedding = embeddingModel.embedAll(segments).content();
        ChromaConfig.embeddingStore(numberToWord(storeId)).addAll(embedding, segments);
    }

    public List<EmbeddingMatch<TextSegment>> search(Long storeName, String query, int maxResults){
        Embedding embedding = embeddingModel.embed(query).content();
        return ChromaConfig.embeddingStore(numberToWord(storeName)).findRelevant(embedding, maxResults);
    }

    public void addCv(Long id, String condensedText) {
        List<TextSegment> segments = Arrays.stream(condensedText.split("\n")).filter(StringUtils::hasText).map(TextSegment::textSegment).toList();
        addDocuments(id, segments);
    }

    private String numberToWord(long d){
        String format = NumberFormat.getNumberInstance().format(d);
        return format
                .replace("1", "one")
                .replace("2", "two")
                .replace("3", "three")
                .replace("4", "four")
                .replace("5", "five")
                .replace("6", "six")
                .replace("7", "seven")
                .replace("8", "eight")
                .replace("9", "nine");
    }
}
