package org.smartjobs.core.utils;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrencyUtilTest {

    public static final List<Integer> INTEGER_LIST = IntStream.range(0, 10).boxed().toList();
    public static final int[] DOUBLED_ARRAY = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};

    @Test
    void testThatMapWillReturnTheMappedResult(){
        List<Integer> updated = ConcurrencyUtil.virtualThreadListMap(INTEGER_LIST, i -> i * 2);
        assertArrayEquals(DOUBLED_ARRAY, updated.stream().mapToInt(i -> i).toArray() );
    }

    @Test
    void testThatForEachWillExecuteAllTheTasksConcurrently() {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        ConcurrencyUtil.virtualThreadListForEach(INTEGER_LIST, _ -> countDownLatch.countDown());
        try {
            assertTrue(countDownLatch.await(10, TimeUnit.MILLISECONDS), "Not all threads counted down the latch");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}