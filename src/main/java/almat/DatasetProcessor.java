package almat;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Orchestrates reading dataset JSON, running KMP, capturing metrics, and producing KMPResult.
 */
public final class DatasetProcessor {

    /**
     * Input model for JSON.
     */
    public static final class InputModel {
        public String dataset;
        public String pattern;
        public String text;
    }

    public static KMPResult process(Path inputFile) throws IOException {
        InputModel model = JsonIO.read(inputFile, InputModel.class);
        OperationCounter.reset();
        long start = System.nanoTime();
        List<Integer> matches = KMPMatcher.search(model.text, model.pattern);
        long elapsed = System.nanoTime() - start;

        return new KMPResult(
                model.dataset,
                model.pattern,
                model.text.length(),
                matches,
                OperationCounter.charComparisons,
                OperationCounter.fallbackSteps,
                OperationCounter.lpsComputations,
                elapsed
        );
    }
}