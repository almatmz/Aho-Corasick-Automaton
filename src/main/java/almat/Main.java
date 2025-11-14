package almat;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Generic driver:
 *  - Discovers all *.json in input/
 *  - Processes them with DatasetProcessor
 *  - Writes per-dataset JSON results into output/output_{dataset}.json
 *  - Writes output/summary.csv (elapsed in milliseconds)
 *
 * This removes any need for hardcoded footballSmall/Medium/Large paths.
 */
public class Main {
    private static final Path INPUT_DIR = Path.of("input");
    private static final Path OUTPUT_DIR = Path.of("output");

    public static void main(String[] args) throws Exception {
        if (!Files.isDirectory(INPUT_DIR)) {
            System.err.println("No input/ directory found. Create 'input/' and add *.json datasets.");
            return;
        }

        Files.createDirectories(OUTPUT_DIR);

        List<Path> inputs = listJsonFiles(INPUT_DIR);
        if (inputs.isEmpty()) {
            System.err.println("No *.json files found in input/.");
            return;
        }

        List<KMPResult> results = new ArrayList<>();

        for (Path in : inputs) {
            try {
                KMPResult r = DatasetProcessor.process(in);
                results.add(r);
                Path outFile = OUTPUT_DIR.resolve("output_" + safeName(r.getDataset()) + ".json");
                writeResult(r, outFile);
                System.out.println("Processed: " + in.getFileName() + " -> " + outFile.getFileName());
            } catch (Exception e) {
                System.err.println("Failed processing " + in + ": " + e.getMessage());
            }
        }

        if (!results.isEmpty()) {
            Path csv = OUTPUT_DIR.resolve("summary.csv");
            writeSummaryCsv(results, csv);
            System.out.println("Summary written to: " + csv.toAbsolutePath());
        } else {
            System.err.println("No results produced (all inputs failed?).");
        }
    }

    private static List<Path> listJsonFiles(Path dir) throws IOException {
        try (var stream = Files.list(dir)) {
            return stream
                    .filter(p -> Files.isRegularFile(p) && p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json"))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    private static String safeName(String dataset) {
        if (dataset == null || dataset.isBlank()) return "unknown";
        // Make a filesystem-friendly suffix for the output filename
        return dataset.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static void writeResult(KMPResult result, Path out) throws IOException {
        JsonIO.write(out, result);
    }

    private static void writeSummaryCsv(List<KMPResult> results, Path out) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("dataset,textLength,patternLength,matches,charComparisons,fallbackSteps,lpsComputations,elapsedMillis\n");
        for (KMPResult r : results) {
            sb.append(r.getDataset()).append(',')
                    .append(r.getTextLength()).append(',')
                    .append(r.getPattern().length()).append(',')
                    .append(r.getMatches().size()).append(',')
                    .append(r.getCharComparisons()).append(',')
                    .append(r.getFallbackSteps()).append(',')
                    .append(r.getLpsComputations()).append(',')
                    .append(String.format(Locale.US, "%.3f", r.getElapsedMillis()))
                    .append('\n');
        }
        Files.writeString(out, sb.toString());
    }
}