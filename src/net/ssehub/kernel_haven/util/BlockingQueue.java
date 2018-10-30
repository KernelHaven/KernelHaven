package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A blocking queue that can send data from one thread to another. One thread can push data into the queue, that will
 * be read from the other thread. If the queue is currently empty, the reading thread will wait until there is data
 * available. The writing thread has to signal that is done (via {@link #end()}), in which case all subsequent read
 * accesses will return <code>null</code> (once the backed up data is depleted). For this reason, the writing thread
 * must never write null into the queue.
 * <br/>
 * <h2>Usage</h2>
 * <b>Reading:</b>
 * This class is {@link Iterable}, so for-each loops can be used to read all data (until the writing thread said, that
 * it is done). Alternatively, the queue can be read until it returns <code>null</code>, e.g. like so:
 * <pre>
 * {@code
 * Element elem;
 * while ((elem = queue.get()) != null) {
 *     ...
 * }}</pre>
 * <b>Writing:</b>
 * Push data in the queue via the {@link #add(Object)} method. Signal the end, by calling {@link #end()}. If
 * {@link #end()} is not called, then the other thread doesn't know when to stop waiting for data, and thus a deadlock
 * can appear.
 * <pre>
 * {@code
 * BlockingQueue<T> queue = new BlockingQueue<>();
 * queue.add(...);
 * ...
 * queue.end();}</pre>
 * <b>Adding further elements</b> after {@link #end()} was called is not possible. If "extending" an existing, ended
 * queue is necessary, create a new one and copy the data over:
 * <pre>
 * {@code
 * BlockingQueue<T> newQueue = new BlockingQueue<>();
 * T elem;
 * while ((elem = oldQueue.get()) != null) {
 *     newQueue.add(elem);
 * }
 * newQueue.add(new_element);
 * ...
 * newQueue.end();}</pre>
 * 
 * @param <T> The type of data that is send between the threads.
 * 
 * @author Adam
 * @author Alice
 *
 */
public class BlockingQueue<T> {

    private @NonNull Queue<@NonNull T> internalQueue;
    
    private @NonNull Semaphore semaphore;
    
    private boolean end;

    /**
     * Creates an empty queue.
     */
    public BlockingQueue() {
        internalQueue = new ArrayDeque<>();
        semaphore = new Semaphore(0, true);
    }
    
    /**
     * Returns the next element in this queue. If the queue is empty, then this waits until
     * the other thread inserts data.
     * 
     * @return The next element in the queue, or <code>null</code> if the other thread
     *      signaled that it does not want to insert any more data.
     */
    public @Nullable T get() {
        T result = null;
        
        try {
            result = get(0);
        } catch (TimeoutException e) {
            // can't happen
        }
        
        return result;
    }
    
    /**
     * Returns the next element in this queue. If the queue is empty, then this waits until
     * the other thread inserts data.
     * 
     * @param timeout The maximum amount of milliseconds to wait until a {@link TimeoutException} is thrown.
     *      0 here means no timeout.
     * @return The next element in the queue, or <code>null</code> if the other thread
     *      signaled that it does not want to insert any more data.
     *      
     * @throws TimeoutException If the timeout exceeded.
     */
    public @Nullable T get(long timeout) throws TimeoutException {
        T result = null;
        
        boolean gotPermit = false;
        boolean waitSuccess = false;
        while (!waitSuccess) {
            try {
                if (timeout > 0) {
                    gotPermit = semaphore.tryAcquire(1, timeout, TimeUnit.MILLISECONDS);
                } else {
                    semaphore.acquire();
                    gotPermit = true;
                }
                waitSuccess = true;
            } catch (InterruptedException e) {
            }
        }
        
        if (!gotPermit) {
            throw new TimeoutException();
        }
        
        synchronized (internalQueue) {
            result = notNull(internalQueue.poll());
        }
        
        return result;
    }
    
    /**
     * Returns, but does not remove, the next element in this queue. If the queue is empty, then this waits until
     * the other thread inserts data.
     * 
     * @return The next element in the queue, or <code>null</code> if the other thread
     *      signaled that it does not want to insert any more data.
     */
    public @Nullable T peek() {
        T result = null;
        
        try {
            result = peek(0);
        } catch (TimeoutException e) {
            // can't happen
        }
        
        return result;
    }
    
    /**
     * Returns, but does not remove, the next element in this queue. If the queue is empty, then this waits until
     * the other thread inserts data.
     * 
     * @param timeout The maximum amount of milliseconds to wait until a {@link TimeoutException} is thrown.
     *      0 here means no timeout.
     * @return The next element in the queue, or <code>null</code> if the other thread
     *      signaled that it does not want to insert any more data.
     *      
     * @throws TimeoutException If the timeout exceeded.
     */
    public @Nullable T peek(long timeout) throws TimeoutException {
        T result = null;
        
        boolean gotPermit = false;
        boolean waitSuccess = false;
        while (!waitSuccess) {
            try {
                if (timeout > 0) {
                    gotPermit = semaphore.tryAcquire(1, timeout, TimeUnit.MILLISECONDS);
                } else {
                    semaphore.acquire();
                    gotPermit = true;
                }
                waitSuccess = true;
            } catch (InterruptedException e) {
            }
        }
        
        if (!gotPermit) {
            throw new TimeoutException();
        }
        
        synchronized (internalQueue) {
            result = notNull(internalQueue.peek());
            
            semaphore.release(); // release after we peeked the element
        }
        
        return result;
    }
    
    /**
     * Adds the specified element to the end of the queue.
     * 
     * @param element The element to add to the queue.
     * 
     * @throws IllegalStateException If {@link #end()} has already been called.
     */
    public void add(@NonNull T element) {
        synchronized (internalQueue) {
            
            if (end) {
                throw new IllegalStateException("Trying to add new elements while end() has already been called");
            }
            
            internalQueue.add(element);
            semaphore.release();
        }
    }
    
    /**
     * Signals that no more data is added after this call. This allows get() to return <code>null</code>
     * once all existing data has been read out.
     */
    public void end() {
        synchronized (internalQueue) {
            end = true;
            semaphore.release(Integer.MAX_VALUE / 2);
        }
    }
    
    /**
     * Returns whether the other thread has signaled that it does not want to send anymore data or not. This does not
     * mean that there are no items left in the queue, but rather that no new items will be added.
     * 
     * @return Whether the other thread signaled the end of this queue.
     */
    public boolean isEnd() {
        synchronized (internalQueue) {
            return end;
        }
    }
    
    /**
     * Returns how many elements are currently in this buffer.
     * 
     * @return The current size of this queue.
     */
    public int getCurrentSize() {
        synchronized (internalQueue) {
            return internalQueue.size();
        }
    }
    
}
