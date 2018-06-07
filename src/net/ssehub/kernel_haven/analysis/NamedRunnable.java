package net.ssehub.kernel_haven.analysis;

/**
 * A {@link Runnable} with a name, which may be used to name the executing thread.
 * @author El-Sharkawy
 *
 */
public interface NamedRunnable extends Runnable {
    
    /**
     * The name of the runnable.
     * @return The name of the thread/runable.
     */
    public String getName();

}
