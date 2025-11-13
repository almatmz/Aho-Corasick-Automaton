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
     * Build LPS array. Instrumentation increments lpsComputations on each char comparison or fallback.
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
                    len = lps[len - 1]; // fallback
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
     * Instrumentation counts charComparisons and fallbackSteps.
     */
    public static List<Integer> search(String text, String pattern) {
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
                    result.add(i - j);
                    j = lps[j - 1]; // allow overlapping matches
                }
            } else {
                if (j != 0) {
                    OperationCounter.fallbackSteps++;
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return result;
    }
}