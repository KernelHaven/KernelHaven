package net.ssehub.kernel_haven.analysis;

import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A component that collects all results from the previous component into one list.
 * 
 * @param <T> The type of result to collect into one list.
 * 
 * @author Adam
 */
public class ListCollectorComponent<T> extends AnalysisComponent<List<T>> {

    private @NonNull AnalysisComponent<T> previousComponent;
    
    /**
     * Creates anew {@link ListCollectorComponent} for the given previous component.
     * 
     * @param config The global configuration.
     * @param previousComponent The previous component
     */
    public ListCollectorComponent(@NonNull Configuration config, @NonNull AnalysisComponent<T> previousComponent) {
        super(config);
        this.previousComponent = previousComponent;
    }

    @Override
    protected void execute() {
        List<T> collected = new LinkedList<>();
        
        T result;
        while ((result = previousComponent.getNextResult()) != null) {
            collected.add(result);
        }
        
        addResult(collected);
    }
    
    @Override
    public @NonNull String getResultName() {
        return previousComponent.getResultName() + " List";
    }

}
