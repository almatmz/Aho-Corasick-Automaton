import almat.KMPMatcher;
import almat.OperationCounter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive correctness and edge-case tests for KMPMatcher.
 */
public class KMPMatcherTest {

    @Test
    @DisplayName("Exact match: text == pattern")
    void testExact() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("needle", "needle");
        assertEquals(List.of(0), pos, "Exact match should start at 0");
        assertTrue(OperationCounter.charComparisons >= 1);
        assertTrue(OperationCounter.lpsComputations > 0);
        // Post-match reset may be zero when lps[last] == 0; that's fine.
    }

    @Test
    @DisplayName("No match scenario")
    void testNoMatch() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("abcdef", "xyz");
        assertTrue(pos.isEmpty(), "No occurrences expected");
        assertTrue(OperationCounter.charComparisons > 0);
        assertTrue(OperationCounter.lpsComputations > 0);
    }

    @Test
    @DisplayName("Multiple matches (non-overlapping)")
    void testMultipleMatches() {
        OperationCounter.reset();
        String text = "abc needle def needle xyz";
        String pattern = "needle";
        List<Integer> pos = KMPMatcher.search(text, pattern);
        assertEquals(List.of(4, 15), pos);
        assertTrue(OperationCounter.charComparisons > 0);
    }

    @Test
    @DisplayName("Overlapping matches (aaaa vs aa -> [0,1,2]) and post-match resets")
    void testOverlapping() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("aaaa", "aa");
        assertEquals(List.of(0, 1, 2), pos);
        // Overlaps are enabled by post-match reset j = lps[j-1], not by mismatches.
        assertTrue(OperationCounter.matchFallbacks >= 2, "Expected post-match resets for overlaps");
        // fallbackSteps may legitimately be zero here.
    }

    @Test
    @DisplayName("Pattern at start, middle, and end")
    void testPatternStartMiddleEnd() {
        OperationCounter.reset();
        String text = "startXYZmiddleXYZendXYZ";
        String pattern = "XYZ";
        List<Integer> pos = KMPMatcher.search(text, pattern);
        assertEquals(List.of(
                text.indexOf("XYZ"),
                text.indexOf("XYZ", text.indexOf("XYZ") + 1),
                text.lastIndexOf("XYZ")
        ), pos);
    }

    @Test
    @DisplayName("Pattern longer than text -> no matches")
    void testPatternLongerThanText() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("short", "muchlongerpattern");
        assertTrue(pos.isEmpty());
    }

    @Test
    @DisplayName("Empty pattern returns no matches (project policy)")
    void testEmptyPattern() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("anything", "");
        assertTrue(pos.isEmpty(), "By design, empty pattern yields no matches");
    }

    @Test
    @DisplayName("Empty text returns no matches")
    void testEmptyText() {
        OperationCounter.reset();
        List<Integer> pos = KMPMatcher.search("", "abc");
        assertTrue(pos.isEmpty());
    }

    @Test
    @DisplayName("Unicode content (non-ASCII) exact match and occurrences")
    void testUnicode() {
        OperationCounter.reset();
        String text = "αβγ-αβγ-αβ";
        String pattern = "αβγ";
        List<Integer> pos = KMPMatcher.search(text, pattern);
        assertEquals(List.of(0, 4), pos);
    }

    @Test
    @DisplayName("Known LPS array example: 'ababaca'")
    void testLpsArray() {
        OperationCounter.reset();
        int[] lps = KMPMatcher.buildLps("ababaca");
        assertArrayEquals(new int[]{0, 0, 1, 2, 3, 0, 1}, lps);
        assertTrue(OperationCounter.lpsComputations > 0);
    }

    @Test
    @DisplayName("Null inputs cause NullPointerException")
    void testNullInputs() {
        OperationCounter.reset();
        assertThrows(NullPointerException.class, () -> KMPMatcher.search(null, "abc"));
        assertThrows(NullPointerException.class, () -> KMPMatcher.search("abc", null));
    }

    @Test
    @DisplayName("Highly repetitive pattern to exercise fallbacks and post-match resets")
    void testRepetitivePatternFallbacks() {
        OperationCounter.reset();
        String text = "aaaaaaaaaaaaaaaaaa"; // 18 'a'
        String pattern = "aaaaa";           // 5 'a'
        List<Integer> pos = KMPMatcher.search(text, pattern);
        assertEquals(18 - 5 + 1, pos.size()); // 14 occurrences
        // With repetitive pattern there will be post-match resets for overlaps
        assertTrue(OperationCounter.matchFallbacks >= 1);
    }
}