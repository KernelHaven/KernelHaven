package net.ssehub.kernel_haven.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeoutException;

import org.junit.Test;

/**
 * Tests the blocking queue.
 * 
 * @author Adam
 * @author Alice
 */
public class BlockingQueueTest {
    
    /**
     * Tests the blocking queue when communicating between two threads.
     * 
     * @throws InterruptedException unwanted.
     */
    @Test
    public void testSync() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>();
        
        Thread t1 = new Thread(() ->  {
            try {
                queue.add("test1");
            
                Thread.sleep(200);
                queue.add("test2");
                
                queue.add("test3");
                queue.add("test4");
                
                Thread.sleep(200);
                queue.end();
                
            } catch (InterruptedException e) {
                fail();
            }
        });
        
        Thread t2 = new Thread(() -> {
            try {
                assertThat(queue.get(), is("test1"));
            
                assertThat(queue.get(), is("test2"));
                
                Thread.sleep(200);
                assertThat(queue.get(), is("test3"));
                assertThat(queue.get(), is("test4"));
                
                assertThat(queue.get(), nullValue());
                assertThat(queue.get(), nullValue());
                
            } catch (InterruptedException e) {
                fail();
            }
        });
        
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
    }
    
    /**
     * Tests the basic queue operations.
     */
    @Test
    public void testBasic() {
        BlockingQueue<String> queue = new BlockingQueue<>();
        
        queue.add("1");
        assertThat(queue.get(), is("1"));
        
        queue.add("2");
        queue.add("3");
        queue.add("4");
        assertThat(queue.get(), is("2"));
        assertThat(queue.get(), is("3"));
        assertThat(queue.get(), is("4"));
        
        queue.add("5");
        queue.add("6");
        assertThat(queue.peek(), is("5"));
        assertThat(queue.get(), is("5"));
        assertThat(queue.peek(), is("6"));
        assertThat(queue.get(), is("6"));
    }
    
    /**
     * Tests whether the blocking queue correctly throws timeout exceptions.
     * 
     * @throws TimeoutException wanted.
     */
    @Test(expected = TimeoutException.class)
    public void testTimeoutException() throws TimeoutException {
        BlockingQueue<String> queue = new BlockingQueue<>();
        queue.get(200);
    }

}
