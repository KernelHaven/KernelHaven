package net.ssehub.kernel_haven.test_utils;

import java.util.Properties;

import net.ssehub.kernel_haven.SetUpException;
import net.ssehub.kernel_haven.analysis.AnalysisComponent;
import net.ssehub.kernel_haven.util.BlockingQueue;

/**
 * An analysis component that provides static data. Useful as input for testing other components.
 * 
 * @param <T> The type of data to create.
 * 
 * @author Adam
 */
public class TestAnalysisComponentProvider<T> extends AnalysisComponent<T> {

    private BlockingQueue<T> data;
    
    /**
     * Creates a new instance with the given data.
     * 
     * @param data The data that this component should "create" and pass to the next component.
     * 
     * @throws SetUpException Shouldn't happen.
     */
    public TestAnalysisComponentProvider(Iterable<T> data) throws SetUpException {
        super(new TestConfiguration(new Properties()));
        this.data = new BlockingQueue<>();
        for (T t : data) {
            this.data.add(t);
        }
        this.data.end();
    }
    
    /**
     * Creates a new instance with the given data.
     * 
     * @param data The data that this component should "create" and pass to the next component.
     * 
     * @throws SetUpException Shouldn't happen.
     */
    @SafeVarargs
    public TestAnalysisComponentProvider(T... data) throws SetUpException {
        super(new TestConfiguration(new Properties()));
        this.data = new BlockingQueue<>();
        for (T t : data) {
            this.data.add(t);
        }
        this.data.end();
    }
        /**
     * Creates a new instance with the given data.
     * 
     * @param data The data that this component should "create" and pass to the next component.
     * 
     * @throws SetUpException Shouldn't happen.
     */
    public TestAnalysisComponentProvider(T data) throws SetUpException {
        super(new TestConfiguration(new Properties()));
        this.data = new BlockingQueue<>();
        this.data.add(data);
        this.data.end();
    }

    @Override
    protected void execute() {
        for (T element : this.data) {
            addResult(element);
        }
    }

    @Override
    public String getResultName() {
        return "TestResult";
    }
    
}
