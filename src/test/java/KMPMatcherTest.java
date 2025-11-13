import almat.KMPMatcher;
import almat.OperationCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Correctness tests for KMPMatcher.
 */
public class KMPMatcherTest {

    @Test
    @DisplayName("Exact match")
    void testExact() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("needle", "needle");
        assertEquals(List.of(0), pos);
        assertTrue(OperationCounter.charComparisons >= "needle".length());
    }

    @Test
    @DisplayName("No match scenario")
    void testNoMatch() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("abcdef", "xyz");
        assertTrue(pos.isEmpty());
    }

    @Test
    @DisplayName("Multiple matches non-overlapping")
    void testMultipleMatches() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("abc needle def needle xyz", "needle");
        assertEquals(List.of(4, 14), pos);
    }

    @Test
    @DisplayName("Overlapping matches (aaaa vs aa)")
    void testOverlapping() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("aaaa", "aa");
        assertEquals(List.of(0,1,2), pos);
        assertTrue(OperationCounter.fallbackSteps > 0);
    }

    @Test
    @DisplayName("LPS correctness sample")
    void testLps() {
        OperationCounter.reset();
        int[] lps = KMPMatcher.buildLps("ababaca");
        assertArrayEquals(new int[]{0,0,1,2,3,0,1}, lps);
    }
}