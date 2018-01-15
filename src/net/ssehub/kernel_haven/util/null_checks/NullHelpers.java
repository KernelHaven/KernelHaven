package net.ssehub.kernel_haven.util.null_checks;

/**
 * Contains helper methods for {@link NonNull} and {@link Nullable} annotations. These are useful when static analysis
 * tools are used. 
 * 
 * @author Adam
 */
public class NullHelpers {

    /**
     * Don't allow any instances.
     */
    public NullHelpers() {
    }
    
    /**
     * Converts a {@link Nullable} (or undefined) value to a {@link NonNull} one. This method should only be called,
     * if the caller is sure that the value must be {@link NonNull}.
     * 
     * @param value The value that should actually be {@link NonNull}.
     * @param msg An error message for the assertion if the value is <code>null</code>.
     * 
     * @return The same value, with a {@link NonNull} annotation.
     * 
     * @throws AssertionError If the value is <code>null</code>.
     */
    public static @NonNull <T> T notNull(@Nullable T value, @Nullable String msg) throws AssertionError {
        if (value == null) {
            throw new AssertionError(msg);
        }
        return value;
    }
    
    /**
     * Converts a {@link Nullable} (or undefined) value to a {@link NonNull} one. This method should only be called,
     * if the caller is sure that the value must be {@link NonNull}.
     * 
     * @param value The value that should actually be {@link NonNull}.
     * 
     * @return The same value, with a {@link NonNull} annotation.
     * 
     * @throws AssertionError If the value is <code>null</code>.
     */
    public static @NonNull <T> T notNull(@Nullable T value) throws AssertionError {
        if (value == null) {
            throw new AssertionError("Supplied value is null, but was expected not null");
        }
        return value;
    }
    
    /**
     * Declares an unmarked value as {@link Nullable}. This may be needed for some calls of methods from libraries.
     * 
     * @param value The value that may be <code>null</code>.
     * @return The {@link Nullable} annotated value.
     */
    public static @Nullable <T> T maybeNull(T value) {
        return value;
    }
    
}
