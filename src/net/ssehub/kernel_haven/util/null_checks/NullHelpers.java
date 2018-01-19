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
    private NullHelpers() {
    }
    
    /**
     * Converts a {@link Nullable} (or undefined) value to a {@link NonNull} one. This method should only be called,
     * if the caller is sure that the value must be {@link NonNull}.
     * 
     * @param <T> The type of the value to convert.
     * 
     * @param value The value that should actually be {@link NonNull}.
     * @param msg An error message for the assertion if the value is <code>null</code>.
     * 
     * @return The same value, with a {@link NonNull} annotation.
     * 
     * @throws AssertionError If the value is <code>null</code>.
     */
    public static final <T> @NonNull T notNull(@Nullable T value, @Nullable String msg) throws AssertionError {
        if (value == null) {
            throw new AssertionError(msg);
        }
        return value;
    }
    
    /**
     * Converts a {@link Nullable} (or undefined) value to a {@link NonNull} one. This method should only be called,
     * if the caller is sure that the value must be {@link NonNull}.
     * 
     * @param <T> The type of the value to convert.
     * 
     * @param value The value that should actually be {@link NonNull}.
     * 
     * @return The same value, with a {@link NonNull} annotation.
     * 
     * @throws AssertionError If the value is <code>null</code>.
     */
    public static final <T> @NonNull T notNull(@Nullable T value) throws AssertionError {
        if (value == null) {
            throw new AssertionError("Supplied value is null, but was expected not null");
        }
        return value;
    }
    
    /**
     * Declares an unmarked value as {@link Nullable}. This may be needed for some calls of methods from libraries.
     * 
     * @param <T> The type of the value to declare.
     * 
     * @param value The value that may be <code>null</code>.
     * @return The {@link Nullable} annotated value.
     */
    public static final <T> @Nullable T maybeNull(T value) {
        return value;
    }
    
    /**
     * Declares that an array reference is {@link NonNull}, but the elements inside are {@link Nullable}.
     * 
     * @param <T> The type of nested elements in the array.
     * 
     * @param array The array to declare as {@link NonNull}, with {@link Nullable} elements.
     * @return The annotated array.
     * 
     * @throws AssertionError If <code>array==null</code>.
     */
    @SuppressWarnings("null")
    public static final <T> @Nullable T @NonNull [] notNullArrayWithNullableContent(T[] array) throws AssertionError {
        if (array == null) {
            throw new AssertionError("Supplied array is null, but was expected not null");
        }
        return array;
    }
    
    /**
     * Declares that an array reference is {@link NonNull}, with the elements inside also {@link NonNull}.
     * 
     * @param <T> The type of nested elements in the array.
     * 
     * @param array The array to declare as {@link NonNull}, with {@link NonNull} elements.
     * @return The annotated array.
     * 
     * @throws AssertionError If <code>array==null</code>. Elements inside the array are <b>not</b> checked.
     */
    @SuppressWarnings("null")
    public static final <T> @NonNull T @NonNull [] notNullArrayWithNotNullContent(T[] array) throws AssertionError {
        if (array == null) {
            throw new AssertionError("Supplied array is null, but was expected not null");
        }
        return array;
    }
    
}
