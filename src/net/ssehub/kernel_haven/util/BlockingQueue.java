package net.ssehub.kernel_haven.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

/**
 * A blocking queue that can send data from one thread to another. Even if this queue is empty, another thread may
 * add further elements in future as long this list dosn't return <tt>null</tt>. <br/>
 * <h2>Usage</h2>
 * <b>Reading:</b>
 * <pre>
 * {@code
 * Element elem;
 * while ((elem = queue.get()) != null) {
 *     ...
 * }}</pre>
 * <b>Writing:</b>
 * <pre>
 * {@code
 * BlockingQueue<T> queue = new BlockingQueue<>();
 * queue.add(...);
 * ...
 * queue.end();}</pre>
 * <b>Adding further elements</b> after {@link #end()} was called is not possible. In this case the queue must
 * be copied:
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
            
            if (end) {
                throw new IllegalStateException("Trying to add new elements while end() has already been called");
            }
            
            internalQueue.add(element);
            internalQueue.notifyAll();
        }
    }
    
    /**
     * Signals that no more data is added after this call. This allows get() to return <code>null</code>
     * once all existing data has been read out.
     */
    public void end() {
        synchronized (internalQueue) {
            end = true;
            internalQueue.notifyAll();
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
