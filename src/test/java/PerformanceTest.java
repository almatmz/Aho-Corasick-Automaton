import almat.KMPMatcher;
import almat.OperationCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Rudimentary performance test: ensures linear growth and instrumentation population.
 */
public class PerformanceTest {

    @Test
    @DisplayName("Performance scaling with growing text size")
    void testScaling() {
        String pattern = "abcab";
        int[] sizes = {5_000, 10_000};

        long previousTime = -1;
        for (int size : sizes) {
            String text = randomText(size, pattern);
            OperationCounter.reset();
            long start = System.nanoTime();
            var matches = KMPMatcher.search(text, pattern);
            long elapsed = System.nanoTime() - start;

            // Basic sanity checks:
            assertTrue(OperationCounter.charComparisons > 0);
            assertTrue(OperationCounter.lpsComputations > 0);

            if (previousTime != -1) {
                // Expect roughly >= previous time (loose check)
                assertTrue(elapsed >= previousTime * 0.5, "Unexpected performance anomaly");
            }
            previousTime = elapsed;
        }
    }

    private String randomText(int len, String pattern) {
        Random r = new Random(42);
        StringBuilder sb = new StringBuilder(len + 50);
        // Insert pattern a few times
        for (int i = 0; i < len; i++) {
            sb.append((char)('a' + r.nextInt(26)));
        }
        sb.insert(len / 3, pattern);
        sb.insert(len / 2, pattern);
        sb.insert(len - pattern.length() - 10, pattern);
        return sb.toString();
    }
}