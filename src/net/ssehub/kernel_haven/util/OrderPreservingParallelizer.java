package net.ssehub.kernel_haven.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * <p>
 * An utility class executes a given function in multiple parallel threads. It also preserves the order of the result;
 * that means that outputs will appear in the same order as their inputs were given.
 * </p>
 * <p>
 * The constructor will get a function and a consumer. The function will turn inputs into outputs. The order in which
 * it will be called is not guaranteed. The function should be thread-safe. The consumer will be called on the outputs
 * of the function. The order of output elements passed to it will be the same as the order of the inputs that produced
 * the outputs. For example, if input1 is transformed into output1, and input2 is transformed into output2, then the
 * consumer will always receive output1 first, even if the function finishes with output2 before output1.
 * </p>
 * <p>
 * Usage could look like this:
 * <code><pre>
 * OrderPreservingParallelizer parallelizer = new OrderPreservingParallelizer(someFunction, someConsumer, 4);
 * 
 * // add inputs
 * parallelizer.add(input1);
 * parallelizer.add(input2);
 * parallelizer.add(input3);
 * 
 * // tell the parallelizer that no more inputs will be send
 * parallelizer.end();
 * 
 * // wait until all inputs are processed
 * parallelizer.join();
 * 
 * </pre></code>
 * </p>
 * 
 * @param <Input> The input data type.
 * @param <Output>> The input data type.
 * 
 * @author Adam
 */
public class OrderPreservingParallelizer<Input, Output> {

    /**
     * A single work package, with the index where it should be added in the result list. 
     */
    private class WorkPackage {
        
        private int index;
        
        private Input input;
        
        private Output output;
        
        /**
         * Creates a {@link WorkPackage}.
         * 
         * @param index The index where this belongs in the output package.
         * @param input The input for the calculation.
         */
        public WorkPackage(int index, Input input) {
            this.index = index;
            this.input = input;
        }
        
        /**
         * Executes this work package.
         */
        public void execute() {
            this.output = function.apply(this.input);
        }
        
        /**
         * Returns the output of the execution.
         * 
         * @return The result of the execution.
         */
        public Output getOutput() {
            return output;
        }
        
        /**
         * Returns the index where the result of this package belongs in the output list.
         * 
         * @return The index where this result belongs.
         */
        public int getIndex() {
            return index;
        }
        
    }
    
    private @NonNull Function<Input, Output> function;
    
    private @NonNull Consumer<Output> conusmer;
    
    private @NonNull BlockingQueue<WorkPackage> todo;
    
    private @NonNull BlockingQueue<WorkPackage> done;
    
    private int numWorkersDone = 0;
    
    private int wpIndex;
    
    private Thread collector;
    
    /**
     * Creates an {@link OrderPreservingParallelizer}.
     * 
     * @param function The function that turns inputs into outputs.
     * @param conusmer The consumer that will receive the outputs.
     * @param numThreads The number of worker threads to spawn.
     */
    public OrderPreservingParallelizer(@NonNull Function<Input, Output> function, @NonNull Consumer<Output> conusmer,
            int numThreads) {
        
        this.function = function;
        this.conusmer = conusmer;
        
        todo = new BlockingQueue<>();
        done = new BlockingQueue<>();
        
        start(numThreads);
    }
    
    /**
     * Starts the worker threads and the collector thread.
     * 
     * @param numThreads The number of worker threads to start.
     */
    private void start(int numThreads) {
        // spawn worker threads
        for (int i = 0; i < numThreads; i++) {
            new Thread(() -> {
                
                WorkPackage wp;
                while ((wp = todo.get()) != null) {
                    wp.execute();
                    done.add(wp);
                }
                
                synchronized (OrderPreservingParallelizer.this) {
                    numWorkersDone++;
                    if (numWorkersDone == numThreads) {
                        done.end();
                    }
                }
                
            }, "ThreadedFeatureEffectFinder-Worker-" + (i + 1)).start();
        }
        
        // spawn collector thread
        collector = new Thread(() -> {
            
            List<@NonNull WorkPackage> received = new LinkedList<>();
            int nextWantedIndex = 0;
            
            WorkPackage wp;
            while ((wp = done.get()) != null) {
                received.add(wp);
                
                boolean found;
                do {
                    found = false;
                    for (WorkPackage possibleNext : received) {
                        if (possibleNext.getIndex() == nextWantedIndex) {
                            // we have found the next result that we can send 
                            found = true;
                            conusmer.accept(possibleNext.getOutput());
                            nextWantedIndex++;
                        }
                    }
                    
                } while (found);
                
            }
            
        }, "ThreadedFeatureEffectFinder-Collector");
        collector.start();
    }
    
    /**
     * Adds another input to be processed. Must not be called after {@link #end()}.
     * 
     * @param input The input to process.
     */
    public void add(Input input) {
        synchronized (this) {
            todo.add(new WorkPackage(wpIndex, input));
            wpIndex++;
        }
    }
    
    /**
     * Specifies that no more inputs will be added. This worker is done after any inputs currently in the queue are
     * done.
     */
    public void end() {
        todo.end();
    }
    
    /**
     * Waits until the last output was passed to the consumer. Note that {@link #end()} must have been called so we now
     * when we are done.
     */
    public void join() {
        try {
            collector.join();
        } catch (InterruptedException e) {
            Logger.get().logException("Cannot wait for thread", e);
        }
    }
    
}
