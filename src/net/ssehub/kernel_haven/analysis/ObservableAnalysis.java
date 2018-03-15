package net.ssehub.kernel_haven.analysis;

import java.util.ArrayList;
import java.util.List;

import net.ssehub.kernel_haven.config.Configuration;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * An {@link AnalysisComponent} which does not produce any results, instead it will pass the received results to
 * observers. This component is intended to serve as an interface between KernelHaven and other tools, which want to
 * use KernelHaven as some kind of input source.
 * @author El-Sharkawy
 *
 * @param <I>
 */
public class ObservableAnalysis<I> extends AnalysisComponent<I> {

    private static List<IAnalysisObserver> observers = new ArrayList<>();

    private @NonNull AnalysisComponent<I> previousComponent;
    
    /**
     * Creates a new analysis component.
     * 
     * @param config The pipeline configuration.
     * @param previousComponent The component to observe
     */
    public ObservableAnalysis(@NonNull Configuration config, @NonNull AnalysisComponent<I> previousComponent) {
        super(config);
        this.previousComponent = previousComponent;
    }

    @Override
    protected void execute() {
        List<@NonNull I> previousResults = new ArrayList<>();
        
        @Nullable I input;
        while ((input = previousComponent.getNextResult()) != null) {
            addResult(input);
            previousResults.add(input);
        }
        
        for (IAnalysisObserver observer : observers) {
            if (!previousResults.isEmpty()) {
                observer.notifyFinished(previousResults);
            } else {
                observer.notifyFinished();
            }
        }
    }

    @Override
    public @NonNull String getResultName() {
        return "Observed " + previousComponent.getResultName();
    }

    /**
     * Sets observers for all instances of this class, will also removed all previously set observers.
     * @param observers All observers to set, must not be <tt>null</tt>.
     */
    public static void setObservers(@NonNull IAnalysisObserver... observers) {
        ObservableAnalysis.observers.clear();
        for (int i = 0; i < observers.length; i++) {
            ObservableAnalysis.observers.add(observers[i]);
        }
    }
}
