package net.ssehub.kernel_haven.code_model;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import net.ssehub.kernel_haven.util.CodeExtractorException;
import net.ssehub.kernel_haven.util.ExtractorException;

/**
 * A CodeModelProvider mock, for testing.
 * @author El-Sharkawy
 *
 */
public class TestCodeModelProvider extends CodeModelProvider {
    
    private Queue<SourceFile> resultQueue;
    
    /**
     * Sole constructor for this class.
     */
    public TestCodeModelProvider() {
        super();
        resultQueue = new LinkedBlockingQueue<>();
    }
    
    @Override
    public void addResult(SourceFile result) {
        resultQueue.add(result);
    }
    
    @Override
    public SourceFile getNext() throws ExtractorException {
        return resultQueue.poll();
    }
    
    @Override
    public CodeExtractorException getNextException() {
        return null;
    }

}
