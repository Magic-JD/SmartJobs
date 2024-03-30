package org.smartjobs.com.utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class ConcurrencyUtil {

    private ConcurrencyUtil() {
        // Private constructor to prevent instantiation.
    }

    public static <T, R> List<R> virtualThreadList(List<T> list, Function<T, R> transform) {
        List<CompletableFuture<R>> completeableFuture = list.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> transform.apply(item), Executors.newVirtualThreadPerTaskExecutor())).toList();
        CompletableFuture.allOf(completeableFuture.toArray(CompletableFuture[]::new)).join();
        return completeableFuture.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
