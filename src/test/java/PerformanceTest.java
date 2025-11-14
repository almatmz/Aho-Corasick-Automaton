import almat.KMPMatcher;
import almat.OperationCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Stable performance test:
 * - Focuses on deterministic scaling via character comparisons (algorithmic work),
 *   avoiding flaky wall-clock time scaling assertions.
 * - Uses warmup and median-of-trials to reduce variance.
 *
 * For rigorous benchmarking, use JMH instead of a unit test.
 */
public class PerformanceTest {

    @Test
    @DisplayName("KMP character comparisons scale with text size (median over trials)")
    void testScalingByComparisons() {
        String pattern = "abcab";
        int[] sizes = {3_000, 6_000, 12_000};

        // Prepare deterministic texts per size
        String[] texts = Arrays.stream(sizes)
                .mapToObj(size -> randomText(size, pattern, 42))
                .toArray(String[]::new);

        // Warmup JIT
        warmup(texts[0], pattern, 3);

        // Measure medians
        int trials = 5; // median over 5 trials
        Measure[] measures = new Measure[texts.length];
        for (int i = 0; i < texts.length; i++) {
            measures[i] = measureMedian(texts[i], pattern, trials);
            // Sanity: time positive and comparisons > 0
            assertTrue(measures[i].nanosMedian > 0, "Elapsed time should be > 0");
            assertTrue(measures[i].charComparisonsMedian > 0, "Char comparisons should be > 0");
            // Comparisons should be on the same order as text length
            long n = texts[i].length();
            assertTrue(measures[i].charComparisonsMedian >= n / 3,
                    "Comparisons unexpectedly low vs text length");
            assertTrue(measures[i].charComparisonsMedian <= n * 5L,
                    "Comparisons unexpectedly high vs text length");
        }

        // Assert scaling by comparisons between consecutive sizes
        for (int i = 1; i < sizes.length; i++) {
            long prevComp = measures[i - 1].charComparisonsMedian;
            long nowComp  = measures[i].charComparisonsMedian;
            double sizeRatio = (double) sizes[i] / sizes[i - 1];

            // Expect comparisons to scale roughly with size; allow broad tolerance
            // This uses a minimal expected ratio of 0.75 * sizeRatio to be resilient.
            double minExpectedRatio = sizeRatio * 0.75;
            double actualRatio = (double) nowComp / prevComp;

            assertTrue(actualRatio >= minExpectedRatio,
                    "Char comparisons did not scale as expected: prev=" + prevComp +
                            ", now=" + nowComp + ", sizeRatio=" + sizeRatio + ", actualRatio=" + actualRatio);
        }
    }

    private record Measure(long nanosMedian, long charComparisonsMedian) {}

    private static void warmup(String text, String pattern, int iterations) {
        for (int i = 0; i < iterations; i++) {
            OperationCounter.reset();
            KMPMatcher.search(text, pattern);
        }
    }

    private static Measure measureMedian(String text, String pattern, int trials) {
        long[] times = new long[trials];
        long[] comps = new long[trials];

        for (int t = 0; t < trials; t++) {
            OperationCounter.reset();
            long start = System.nanoTime();
            KMPMatcher.search(text, pattern);
            long elapsed = System.nanoTime() - start;

            times[t] = elapsed;
            comps[t] = OperationCounter.charComparisons;
        }

        Arrays.sort(times);
        Arrays.sort(comps);
        return new Measure(times[trials / 2], comps[trials / 2]);
    }

    private static String randomText(int len, String pattern, long seed) {
        Random r = new Random(seed);
        StringBuilder sb = new StringBuilder(len + 50);
        for (int i = 0; i < len; i++) {
            sb.append((char) ('a' + r.nextInt(26)));
        }
        // Insert the pattern at deterministic positions to ensure some matches
        if (len > pattern.length() + 20) {
            sb.insert(len / 3, pattern);
            sb.insert(len / 2, pattern);
            sb.insert(len - pattern.length() - 10, pattern);
        }
        return sb.toString();
    }
}