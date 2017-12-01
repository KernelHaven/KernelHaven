package net.ssehub.kernel_haven.util.logic;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import net.ssehub.kernel_haven.util.Logger;

/**
 * Tests the {@link DisjunctionQueue}.
 * @author El-Sharkawy
 *
 */
public class DisjunctionQueueTests {

    /**
     * Tests the general ability to create disjunctions, without any simplifications.
     */
    @Test
    public void  testCreateDisjunction() {
        DisjunctionQueue queue = new DisjunctionQueue(false);
        
        // 1st test with constant values
        queue.add(True.INSTANCE);
        queue.add(False.INSTANCE);
        Formula f = queue.getDisjunction();
        Assert.assertEquals(new Disjunction(True.INSTANCE, False.INSTANCE), f);
        
        // 2nd test with variable
        Variable varA = new Variable("A");
        queue.add(varA);
        queue.add(varA);
        f = queue.getDisjunction();
        Assert.assertEquals(new Disjunction(varA, varA), f);
    }
    
    /**
     * Tests if can simplify constantly true parts.
     */
    @Test
    public void  testSimplifyTrue() {
        DisjunctionQueue queue = new DisjunctionQueue(true);
        
        // 1st test: Add true first
        queue.add(True.INSTANCE);
        queue.add(False.INSTANCE);
        Formula f = queue.getDisjunction();
        Assert.assertEquals(True.INSTANCE, f);
        
        // 2nd test: Add true last
        queue.add(False.INSTANCE);
        queue.add(True.INSTANCE);
        f = queue.getDisjunction();
        Assert.assertEquals(True.INSTANCE, f);
    }
    
    /**
     * Test that it avoids insertion of the same element twice.
     */
    @Test
    public void  testAvoidDoubledElements() {
        DisjunctionQueue queue = new DisjunctionQueue(true);
        
        Variable varA = new Variable("A");
        queue.add(varA);
        queue.add(varA);
        Formula f = queue.getDisjunction();
        Assert.assertEquals(varA, f);
    }
    
    /**
     * Tests if a simplifier can be used.
     */
    @Test
    public void  testUseSimplifier() {
        /* 
         * A simplifier mock (will change the result to a fixed expression),
         * not a real simplifier but sufficient for testing
         */
        Variable varB = new Variable("B");
        DisjunctionQueue queue = new DisjunctionQueue(f -> varB);
        
        Variable varA = new Variable("A");
        queue.add(varA);
        Formula f = queue.getDisjunction();
        Assert.assertSame(varB, f);
    }
    
    /**
     * Tests that the {@link DisjunctionQueue} produces no error log in case of correct optimizations.
     * Based on detect bug.
     */
    @Test
    public void testNoErrorLogWhenSimplifyingTrue() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Logger.init(buffer);
        
        DisjunctionQueue queue = new DisjunctionQueue(true);
        queue.add(new Variable("X"));
        queue.add(True.INSTANCE);
        queue.getDisjunction("Test Case");
        
        String log = buffer.toString();
        Assert.assertTrue("Error log produced even if there was no error: " + log, log.isEmpty());
    }
}
