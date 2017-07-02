package net.ssehub.kernel_haven.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

/**
 * A blocking queue that can send data from one thread to another.
 * 
 * @param <T> The type of data that is send between the threads.
 * 
 * @author Adam
 * @author Alice
 *
 */
public class BlockingQueue<T> implements Iterable<T> {

    private Queue<T> internalQueue;
    
    private boolean end;

    /**
     * Creates an empty queue.
     */
    public BlockingQueue() {
        internalQueue = new LinkedList<>();
    }
    
    /**
     * Returns the next element in this queue. If the queue is empty, then this waits until
     * the other thread inserts data.
     * 
     * @return The next element in the queue, or <code>null</code> if the other thread
     *      signaled that it does not want to insert any more data.
     */
    public T get() {
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
    public T get(long timeout) throws TimeoutException {
        T result = null;
        
        synchronized (internalQueue) {
            if (internalQueue.isEmpty() && !end) {
                boolean waitSuccess = false;
                while (!waitSuccess) {
                    try {
                        internalQueue.wait(timeout);
                        waitSuccess = true;
                    } catch (InterruptedException e) {
                    }
                }
            }
            
            result = internalQueue.poll();
            
            if (result == null && !end) {
                throw new TimeoutException();
            }
            
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
    public T peek() {
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
    public T peek(long timeout) throws TimeoutException {
        T result = null;
        
        synchronized (internalQueue) {
            if (internalQueue.isEmpty() && !end) {
                boolean waitSuccess = false;
                while (!waitSuccess) {
                    try {
                        internalQueue.wait(timeout);
                        waitSuccess = true;
                    } catch (InterruptedException e) {
                    }
                }
            }
            
            result = internalQueue.peek();
            
            if (result == null && !end) {
                throw new TimeoutException();
            }
            
        }
        
        return result;
    }
    
    /**
     * Adds the specified element to the end of the queue.
     * 
     * @param element The element to add to the queue.
     */
    public void add(T element) {
        synchronized (internalQueue) {
            internalQueue.add(element);
            internalQueue.notify();
        }
    }
    
    /**
     * Signals that no more data is added after this call. This allows get() to return <code>null</code>
     * once all existing data has been read out.
     */
    public void end() {
        synchronized (internalQueue) {
            end = true;
            internalQueue.notify();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return peek() != null;
            }

            @Override
            public T next() {
                return get();
            }
        };
    }
    
}
