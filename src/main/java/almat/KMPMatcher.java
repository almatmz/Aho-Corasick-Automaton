package almat;

import java.util.ArrayList;
import java.util.List;

/**
 * Knuth–Morris–Pratt Matcher with instrumentation.
 * Time: O(n + m)
 * Space: O(m)
 */
public final class KMPMatcher {

    private KMPMatcher() {}

    /**
     * Build the LPS array for the given pattern.
     * Increments OperationCounter.lpsComputations on each step involving a comparison/fallback.
     */
    public static int[] buildLps(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0; // length of current longest prefix-suffix
        int i = 1;

        while (i < m) {
            OperationCounter.lpsComputations++;
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1]; // fallback within LPS construction
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /**
     * Returns start indices of all occurrences of pattern in text.
     * Instrumentation:
     *  - charComparisons incremented on each character comparison
     *  - fallbackSteps incremented on mismatch-driven fallback
     *  - matchFallbacks incremented when resetting j after a successful match (enables overlaps)
     */
    public static List<Integer> search(String text, String pattern) {
        // NPEs intentionally thrown if text or pattern is null (caught by tests)
        List<Integer> result = new ArrayList<>();
        if (pattern.isEmpty() || text.isEmpty()) {
            return result;
        }
        int[] lps = buildLps(pattern);
        int i = 0; // text index
        int j = 0; // pattern index

        while (i < text.length()) {
            OperationCounter.charComparisons++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == pattern.length()) {
                    // Found a full match
                    result.add(i - j);
                    // Post-match reset allows overlapping matches
                    int nextJ = lps[j - 1];
                    if (nextJ != 0) {
                        OperationCounter.matchFallbacks++; // this is the overlap-enabling reset
                    }
                    j = nextJ;
                }
            } else {
                if (j != 0) {
                    // Mismatch fallback driven by LPS
                    OperationCounter.fallbackSteps++;
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return result;
    }

    public static List<Integer> match(String text, String pattern) {
        return search(text, pattern);
    }
}