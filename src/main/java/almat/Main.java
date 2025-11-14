package almat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Main driver:
 *  - Processes small, medium, large inputs.
 *  - Writes outputs JSON + summary CSV.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Path small = Path.of("input/small_strings.json");
        Path medium = Path.of("input/medium_strings.json");
        Path large = Path.of("input/large_strings.json");

        KMPResult rSmall = DatasetProcessor.process(small);
        KMPResult rMedium = DatasetProcessor.process(medium);
        KMPResult rLarge = DatasetProcessor.process(large);

        writeResult(rSmall, Path.of("output/output_small_strings.json"));
        writeResult(rMedium, Path.of("output/output_medium_strings.json"));
        writeResult(rLarge, Path.of("output/output_large_strings.json"));

        writeSummaryCsv(new KMPResult[]{rSmall, rMedium, rLarge}, Path.of("output/summary.csv"));

        System.out.println("Processing complete. See output/ directory.");
    }

    private static void writeResult(KMPResult result, Path out) throws IOException {
        JsonIO.write(out, result);
    }

    private static void writeSummaryCsv(KMPResult[] results, Path out) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("dataset,textLength,patternLength,matches,charComparisons,fallbackSteps,lpsComputations,elapsedNanos\n");
        for (KMPResult r : results) {
            sb.append(r.getDataset()).append(',')
                    .append(r.getTextLength()).append(',')
                    .append(r.getPattern().length()).append(',')
                    .append(r.getMatches().size()).append(',')
                    .append(r.getCharComparisons()).append(',')
                    .append(r.getFallbackSteps()).append(',')
                    .append(r.getLpsComputations()).append(',')
                    .append(r.getElapsedNanos())
                    .append('\n');
        }
        Files.writeString(out, sb.toString());
    }
}