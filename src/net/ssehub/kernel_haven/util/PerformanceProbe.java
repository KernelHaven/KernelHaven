/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.config.DefaultSettings;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A utility class for performance measurements. This is especially useful for measuring code that is called a lot,
 * since all measurements are aggregated to min/avg/med/max at the end.
 * <p>
 * Measurements are done for "contexts", which are simply string identifiers for the code to measure. All measurements
 * are aggregated per context at the end (when {@link #printResult()} is called).
 *  
 * @author Adam
 */
public final class PerformanceProbe implements Closeable {
    
    private static boolean enabled = false;
    
    private static @NonNull Map<String, Deque<@NonNull PerformanceProbe>> probes = new ConcurrentHashMap<>(500);
    
    private @NonNull String context;
    
    private Map<String, Double> extraData;
    
    private long tStart;
    
    private long tEnd;
    
    /**
     * Creates a new performance probe for a single measurement.
     * 
     * @param context The context that is measured.
     */
    public PerformanceProbe(@NonNull String context) {
        this.context = context;
        
        if (isEnabled(context)) {
            this.extraData = new HashMap<>();
            this.tStart = System.nanoTime();
        }
    }
    
    /**
     * Adds an additional bit of data to this probe. This additional data will be aggregated based on the context of 
     * this probe and the given type. Each performance probe may only contain one data point per type.
     * 
     * @param type The type of data that is provided. 
     * @param value The additional data.
     */
    public void addExtraData(@NonNull String type, double value) {
        if (isEnabled(context)) {
            this.extraData.put(type, value);
        }
    }
    
    /**
     * Signals the end of this measurement.
     */
    @Override
    public void close() {
        if (isEnabled(context)) {
            this.tEnd = System.nanoTime();
            
            Deque<@NonNull PerformanceProbe> newList = new ConcurrentLinkedDeque<>();
            Deque<@NonNull PerformanceProbe> list = probes.putIfAbsent(context, newList);
            if (list == null) {
                list = newList;
            }
            list.add(this);
        }
    }
    
    /**
     * Returns the elapsed time of this probe, in nanoseconds.
     * 
     * @return The elapsed time.
     */
    private long getElapsed() {
        return tEnd - tStart;
    }
    
    /**
     * Determines if a measurement for the given context should be done.
     * 
     * @param context The context to measure.
     * 
     * @return Whether to measure the given context.
     */
    private static boolean isEnabled(@NonNull String context) {
        return enabled; // TODO: add mechanism to en-/disable specific probe contexts
    }
    
    /**
     * Aggregates the given list of doubles and adds the result strings to the given string list.
     * 
     * @param list The list of doubles to aggregate.
     * @param lines The list to add the result lines to.
     */
    private static void aggregateList(@NonNull List<Double> list, @NonNull List<@NonNull String> lines) {
        double[] values = new double[list.size()];
        
        double sum = 0;
        int i = 0;
        for (Double d : list) {
            values[i++] = d;
            sum += d;
        }

        Arrays.sort(values);
        
        double min = values[0];
        double max = values[values.length - 1];
        double avg = sum / values.length;
        double med;
        if (values.length % 2 == 0) {
            med = (values[values.length / 2 - 1] + values[values.length / 2]) / 2.0;
        } else {
            med = values[values.length / 2];
        }
        
        lines.add("          Num Measures: " + values.length);
        lines.add("          Min: " + min);
        lines.add("          Med: " + med);
        lines.add("          Avg: " + avg);
        lines.add("          Max: " + max);
        lines.add("          Sum: " + sum);
    }
    
    /**
     * Aggregates the measurements per context and prints it to the {@link Logger}.
     */
    public static void printResult() {
        if (probes.isEmpty()) {
            return;
        }
        
        List<@NonNull String> lines = new ArrayList<>(probes.size() * 20 + 1);
        lines.add("Performance Measurements:");
        
        probes.entrySet().stream()
            .sorted((e1, e2) -> e1.getKey().compareToIgnoreCase(e2.getKey()))
            .forEach((entry) -> {
                String context = notNull(entry.getKey());
                long[] timeValues = new long[entry.getValue().size()];
                
                Map<String, ArrayList<Double>> extraData = new HashMap<>();
                
                long tSum = 0;
                int i = 0;
                for (PerformanceProbe p : notNull(entry.getValue())) {
                    timeValues[i++] = p.getElapsed();
                    tSum += p.getElapsed();
                    
                    for (Map.Entry<String, Double> ed : p.extraData.entrySet()) {
                        extraData.putIfAbsent(ed.getKey(), new ArrayList<>());
                        extraData.get(ed.getKey()).add(ed.getValue());
                    }
                }
                
                Arrays.sort(timeValues);
                
                long tMin = timeValues[0];
                long tMax = timeValues[timeValues.length - 1];
                double tAvg = (double) tSum / timeValues.length;
                double tMed;
                if (timeValues.length % 2 == 0) {
                    tMed = (timeValues[timeValues.length / 2 - 1] + timeValues[timeValues.length / 2]) / 2.0;
                } else {
                    tMed = timeValues[timeValues.length / 2];
                }
                
                lines.add("  " + context);
                lines.add("      Time:");
                lines.add("          Num Measures: " + timeValues.length);
                lines.add("          Min: " + Util.formatDurationMs(tMin / 1000000));
                lines.add("          Med: " + Util.formatDurationMs((long) tMed / 1000000));
                lines.add("          Avg: " + Util.formatDurationMs((long) tAvg / 1000000));
                lines.add("          Max: " + Util.formatDurationMs(tMax / 1000000));
                lines.add("          Sum: " + Util.formatDurationMs(tSum / 1000000));
                
                for (Map.Entry<String, ArrayList<Double>> ed : extraData.entrySet()) {
                    lines.add("      " + ed.getKey() + ":");
                    aggregateList(notNull(ed.getValue()), lines);
                }
            });
        
        
        Logger.get().logInfo(lines.toArray(new String[0]));
    }
    
    /**
     * Clears all probes. Used mostly in test cases.
     */
    public static void clear() {
        probes.clear();
    }
    
    /**
     * Initializes this class with the given configuration. Determines whether performance measurements should be
     * enabled.
     * 
     * @param config The pipeline configuration.
     */
    public static void initialize(@NonNull Configuration config) {
        enabled = config.getValue(DefaultSettings.MEASURE_PERFORMANCE);
    }
    
    /**
     * En- or disables the performance measurements. This overrides the setting read in
     * {@link #initialize(Configuration)}.
     *  
     * @param enabled Whether the measurements should be enabled.
     */
    public static void setEnabled(boolean enabled) {
        PerformanceProbe.enabled = enabled;
    }

}
