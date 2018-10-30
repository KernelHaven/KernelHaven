package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

/**
 * Tests the {@link OrderPreservingParallelizer} class.
 *
 * @author Adam
 */
public class OrderPreservingParallelizerTest {

    /**
     * Tests whether a single element is processed correctly in one worker thread.
     */
    @Test(timeout = 5000)
    public void testSingleElementInOneThread() {
        List<Character> result = new LinkedList<>();
        
        OrderPreservingParallelizer<Integer, Character> parallelizer = new OrderPreservingParallelizer<>(
            (input) -> (char) ('a' + input - 1),    // function: turn 1 into 'a', 2 into 'b', etc. 
            (character) -> result.add(character),   // consumer: add to result list
            1
        );
        
        parallelizer.add(4);
        parallelizer.end();
        parallelizer.join();
        
        assertThat(result, is(Arrays.asList('d')));
    }
    
    /**
     * Tests whether multiple elements are processed correctly in one worker thread.
     */
    @Test(timeout = 5000)
    public void testMultipleElementsInOneThread() {
        List<Character> result = new LinkedList<>();
        
        OrderPreservingParallelizer<Integer, Character> parallelizer = new OrderPreservingParallelizer<>(
            (input) -> (char) ('a' + input - 1),    // function: turn 1 into 'a', 2 into 'b', etc. 
            (character) -> result.add(character),   // consumer: add to result list
            1
        );
        
        parallelizer.add(4);
        parallelizer.add(7);
        parallelizer.add(2);
        parallelizer.add(4);
        parallelizer.end();
        parallelizer.join();
        
        assertThat(result, is(Arrays.asList('d', 'g', 'b', 'd')));
    }
    
    /**
     * Tests whether many elements are processed correctly in one worker thread.
     */
    @Test(timeout = 5000)
    public void testManyElementsInOneThread() {
        List<Character> result = new LinkedList<>();
        
        OrderPreservingParallelizer<Integer, Character> parallelizer = new OrderPreservingParallelizer<>(
            (input) -> (char) ('a' + input - 1),    // function: turn 1 into 'a', 2 into 'b', etc. 
            (character) -> result.add(character),   // consumer: add to result list
            1
        );
        
        List<Character> expected = new LinkedList<>();
        for (int i = 1; i <= 26; i++) {
            parallelizer.add(i);
            expected.add((char) ('a' + i - 1));
        }
        parallelizer.end();
        parallelizer.join();
        
        assertThat(result, is(expected));
    }
    
    /**
     * Tests whether a single element is processed correctly in multiple worker threads.
     */
    @Test(timeout = 5000)
    public void testSingleElementInMultipleThreads() {
        List<Character> result = new LinkedList<>();
        
        OrderPreservingParallelizer<Integer, Character> parallelizer = new OrderPreservingParallelizer<>(
            (input) -> (char) ('a' + input - 1),    // function: turn 1 into 'a', 2 into 'b', etc. 
            (character) -> result.add(character),   // consumer: add to result list
            4
        );
        
        parallelizer.add(4);
        parallelizer.end();
        parallelizer.join();
        
        assertThat(result, is(Arrays.asList('d')));
    }
    
    /**
     * Tests whether multiple elements are processed correctly in multiple worker threads.
     */
    @Test(timeout = 5000)
    public void testMultipleElementsInMultipleThreads() {
        List<Character> result = new LinkedList<>();
        
        OrderPreservingParallelizer<Integer, Character> parallelizer = new OrderPreservingParallelizer<>(
            (input) -> (char) ('a' + input - 1),    // function: turn 1 into 'a', 2 into 'b', etc. 
            (character) -> result.add(character),   // consumer: add to result list
            4
        );
        
        parallelizer.add(4);
        parallelizer.add(7);
        parallelizer.add(2);
        parallelizer.add(4);
        parallelizer.end();
        parallelizer.join();
        
        assertThat(result, is(Arrays.asList('d', 'g', 'b', 'd')));
    }
    
    /**
     * Tests whether many elements are processed correctly in multiple worker threads.
     */
    @Test(timeout = 5000)
    public void testManyElementsInMultiplehreads() {
        List<Character> result = new LinkedList<>();
        
        OrderPreservingParallelizer<Integer, Character> parallelizer = new OrderPreservingParallelizer<>(
            (input) -> (char) ('a' + input - 1),    // function: turn 1 into 'a', 2 into 'b', etc. 
            (character) -> result.add(character),   // consumer: add to result list
            4
        );
        
        List<Character> expected = new LinkedList<>();
        for (int i = 1; i <= 26; i++) {
            parallelizer.add(i);
            expected.add((char) ('a' + i - 1));
        }
        parallelizer.end();
        parallelizer.join();
        
        assertThat(result, is(expected));
    }
    
    /**
     * Tests whether results that are calculated out-of-order will be passed in correct order to the consumer.
     */
    @Test(timeout = 5000)
    public void testOutOfOrderInOneThread() {
        List<Character> result = new LinkedList<>();
        
        OrderPreservingParallelizer<Integer, Character> parallelizer = new OrderPreservingParallelizer<>(
            (input) -> {                            // function: turn 1 into 'a', 2 into 'b', etc.
                if (input == 1 || input == 3) {     // sleep for 1st and 3rd argument, so 2nd and 4th will be done first
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }
                }
                return (char) ('a' + input - 1);     
            },
            (character) -> result.add(character),   // consumer: add to result list
            1
        );
        
        parallelizer.add(1);
        parallelizer.add(2);
        parallelizer.add(3);
        parallelizer.add(4);
        parallelizer.end();
        parallelizer.join();
        
        assertThat(result, is(Arrays.asList('a', 'b', 'c', 'd')));
    }
    
    /**
     * Tests whether results that are calculated out-of-order will be passed in correct order to the consumer.
     */
    @Test(timeout = 5000)
    public void testOutOfOrderInManyThreads() {
        List<Character> result = new LinkedList<>();
        
        OrderPreservingParallelizer<Integer, Character> parallelizer = new OrderPreservingParallelizer<>(
            (input) -> {                            // function: turn 1 into 'a', 2 into 'b', etc.
                if (input == 1 || input == 3) {     // sleep for 1st and 3rd argument, so 2nd and 4th will be done first
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                return (char) ('a' + input - 1);     
            },
            (character) -> result.add(character),   // consumer: add to result list
            4
        );
        
        parallelizer.add(1);
        parallelizer.add(2);
        parallelizer.add(3);
        parallelizer.add(4);
        parallelizer.end();
        parallelizer.join();
        
        assertThat(result, is(Arrays.asList('a', 'b', 'c', 'd')));
    }
    
    /**
     * Tests whether calling add after end correctly throws an exception.
     */
    @Test(expected = IllegalStateException.class, timeout = 5000)
    public void testAddAfterEnd() {
        OrderPreservingParallelizer<Integer, Character> parallelizer = new OrderPreservingParallelizer<>(
            (input) -> (char) ('a' + input - 1),    // function: turn 1 into 'a', 2 into 'b', etc. 
            (character) -> { },                     // consumer: to nothing
            1
        );
        
        parallelizer.add(1);
        parallelizer.end();
        parallelizer.add(2);
    }
    
    /**
     * Tests whether trying to create this with 0 threads fails.
     */
    @Test(expected = IllegalArgumentException.class, timeout = 5000)
    public void test0NumberOfThreads() {
        new OrderPreservingParallelizer<Integer, Character>(
            (input) -> (char) ('a' + input - 1),    // function: turn 1 into 'a', 2 into 'b', etc. 
            (character) -> { },                     // consumer: to nothing
            0
        );
    }
    
    /**
     * Tests whether trying to create this with -2 threads fails.
     */
    @Test(expected = IllegalArgumentException.class, timeout = 5000)
    public void testNegativeNumberOfThreads() {
        new OrderPreservingParallelizer<Integer, Character>(
            (input) -> (char) ('a' + input - 1),    // function: turn 1 into 'a', 2 into 'b', etc. 
            (character) -> { },                     // consumer: to nothing
            -2
        );
    }
    
}
