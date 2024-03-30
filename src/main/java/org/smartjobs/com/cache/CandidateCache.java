package org.smartjobs.com.cache;

import org.smartjobs.com.service.candidate.data.CandidateData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class CandidateCache {

    private static final Map<String, List<CandidateData>> candidates = new ConcurrentHashMap<>();


    public List<CandidateData> getFromCacheOrCompute(String username, Function<String, List<CandidateData>> compute) {
        return candidates.computeIfAbsent(username, compute);
    }

    public void clearCache(String username) {
        candidates.remove(username);
    }
}
