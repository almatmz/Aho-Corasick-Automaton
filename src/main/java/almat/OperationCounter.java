package almat;

/**
 * Tracks instrumentation for KMP operations.
 * - charComparisons: number of direct text vs pattern character comparisons
 * - lpsComputations: steps taken while building the LPS array
 * - fallbackSteps:   number of mismatch-driven fallbacks during search (j = lps[j-1] on mismatch)
 * - matchFallbacks:  number of post-match resets to allow overlaps (j = lps[j-1] after a full match)
 */
public final class OperationCounter {
    private OperationCounter() {}

    public static long charComparisons = 0;
    public static long lpsComputations = 0;
    public static long fallbackSteps = 0;
    public static long matchFallbacks = 0;

    public static void reset() {
        charComparisons = 0;
        lpsComputations = 0;
        fallbackSteps = 0;
        matchFallbacks = 0;
    }
}