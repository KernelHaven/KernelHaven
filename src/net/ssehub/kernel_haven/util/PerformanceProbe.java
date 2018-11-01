package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * <p>
 * A utility class for performance measurements. This is especially useful for measuring code that is called a lot,
 * since all measurements are aggregated to min/avg/med/max at the end.
 * </p>
 * <p>
 * Measurements are done for "contexts", which are simply string identifiers for the code to measure. All measurements
 * are aggregated per context at the end (when {@link #printResult()} is called).
 * </p>
 *  
 * @author Adam
 */
public class PerformanceProbe implements Closeable {
    
    private static @NonNull Map<String, Deque<@NonNull Long>> measures = new ConcurrentHashMap<>(500);
    
    private @Nullable Deque<@NonNull Long> resultList;
    
    private long tStart;
    
    private long tEnd;
    
    /**
     * Creates a new performance probe for a single measurement.
     * 
     * @param context The context that is measured.
     */
    public PerformanceProbe(@NonNull String context) {
        if (isEnabled(context)) {
            Deque<@NonNull Long> newList = new ConcurrentLinkedDeque<>();
            this.resultList = measures.putIfAbsent(context, newList);
            if (this.resultList == null) {
                this.resultList = newList;
            }
            tStart = System.nanoTime();
        }
    }
    
    /**
     * Signals the end of this measurement.
     */
    @Override
    public void close() {
        tEnd = System.nanoTime();
        
        if (resultList != null) {
            resultList.add(tEnd - tStart);
        }
    }
    
    /**
     * Determines if a measurement for the given context should be done.
     * 
     * @param context The context to measure.
     * 
     * @return Whether to measure the given context.
     */
    private static boolean isEnabled(@NonNull String context) {
        return true; // TODO: implement mechanism to disable contexts
    }
    
    /**
     * Aggregates the measurements per context and prints it to the {@link Logger}.
     */
    public static void printResult() {
        List<@NonNull String> lines = new ArrayList<>(measures.size() * 5 + 1);
        lines.add("Performance Measurements:");
        
        measures.entrySet().stream()
            .sorted((e1, e2) -> e1.getKey().compareToIgnoreCase(e2.getKey()))
            .forEach((entry) -> {
                String context = notNull(entry.getKey());
                long[] values = new long[entry.getValue().size()];
                
                double avg = 0;
                int i = 0;
                for (Long l : notNull(entry.getValue())) {
                    values[i++] = l;
                    avg += (l - avg) / i;
                }
                
                Arrays.sort(values);
                
                long min = values[0];
                long max = values[values.length - 1];
                double med;
                if (values.length % 2 == 0) {
                    med = (values[values.length / 2 - 1] + values[values.length / 2]) / 2.0;
                } else {
                    med = values[values.length / 2];
                }
                
                Logger.get().logDebug(context + " (in nanoseconds): " + entry.getValue());
                
                lines.add("    " + context);
                lines.add("        Min: " + Util.formatDurationMs(min / 1000000));
                lines.add("        Med: " + Util.formatDurationMs((long) med / 1000000));
                lines.add("        Avg: " + Util.formatDurationMs((long) avg / 1000000));
                lines.add("        Max: " + Util.formatDurationMs(max / 1000000));
            });
        
        
        Logger.get().logInfo(lines.toArray(new String[0]));
    }

}
