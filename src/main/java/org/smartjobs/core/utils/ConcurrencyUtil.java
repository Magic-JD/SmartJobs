package org.smartjobs.core.utils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConcurrencyUtil {

    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    private ConcurrencyUtil() {
        // Private constructor to prevent instantiation.
    }

    public static <T, R> List<R> virtualThreadListMap(Collection<T> list, Function<T, R> transform) {
        List<CompletableFuture<R>> completableFuture = list.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> transform.apply(item), executorService)).toList();
        CompletableFuture.allOf(completableFuture.toArray(CompletableFuture[]::new)).join();
        return completableFuture.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    public static <T> void virtualThreadListForEach(Collection<T> list, Consumer<T> consumer) {
        list.forEach(i -> CompletableFuture.runAsync(() -> consumer.accept(i), executorService));
    }
}
