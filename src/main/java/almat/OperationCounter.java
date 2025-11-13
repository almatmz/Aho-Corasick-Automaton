package almat;

/**
 * Tracks instrumentation for KMP operations.
 * All counters are static for simplicity; reset before each run.
 */
public final class OperationCounter {
    private OperationCounter() {}

    public static long charComparisons = 0;
    public static long lpsComputations = 0;
    public static long fallbackSteps = 0;

    public static void reset() {
        charComparisons = 0;
        lpsComputations = 0;
        fallbackSteps = 0;
    }
}