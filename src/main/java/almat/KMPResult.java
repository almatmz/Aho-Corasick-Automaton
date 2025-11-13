package almat;

import java.util.List;

/**
 * Immutable result object capturing matches and instrumentation metrics.
 */
public final class KMPResult {
    private final String dataset;
    private final String pattern;
    private final int textLength;
    private final List<Integer> matches;
    private final long charComparisons;
    private final long fallbackSteps;
    private final long lpsComputations;
    private final long elapsedNanos;

    public KMPResult(String dataset,
                     String pattern,
                     int textLength,
                     List<Integer> matches,
                     long charComparisons,
                     long fallbackSteps,
                     long lpsComputations,
                     long elapsedNanos) {
        this.dataset = dataset;
        this.pattern = pattern;
        this.textLength = textLength;
        this.matches = matches;
        this.charComparisons = charComparisons;
        this.fallbackSteps = fallbackSteps;
        this.lpsComputations = lpsComputations;
        this.elapsedNanos = elapsedNanos;
    }

    public String getDataset() { return dataset; }
    public String getPattern() { return pattern; }
    public int getTextLength() { return textLength; }
    public List<Integer> getMatches() { return matches; }
    public long getCharComparisons() { return charComparisons; }
    public long getFallbackSteps() { return fallbackSteps; }
    public long getLpsComputations() { return lpsComputations; }
    public long getElapsedNanos() { return elapsedNanos; }
}