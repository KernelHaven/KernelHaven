package net.ssehub.kernel_haven.util.logic;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.function.Function;

import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Static utility method for simplifying {@link Formula}s. By default, this does nothing. However, a simplifier can be
 * registered via {@link #setSimplifier(Function)}.
 *
 * @author Adam
 */
public class FormulaSimplifier {

    private static @NonNull Function<@NonNull Formula, @NonNull Formula> simplifier = f -> f;
    
    private static boolean deafultSimplifier = true;
    
    /**
     * Changes the simplifier to be used. Overrides the previous one.
     * 
     * @param simplifier The new simplifier to use.
     */
    public static void setSimplifier(@NonNull Function<@NonNull Formula, @NonNull Formula> simplifier) {
        FormulaSimplifier.simplifier = simplifier;
        deafultSimplifier = false;
    }
    
    /**
     * Whether we still have the default simplifier that does nothing.
     * 
     * @return <code>true</code> if we still have the default simplifier; <code>false</code> if
     *      {@link #setSimplifier(Function)} has been called.
     */
    public static boolean hasDefaultSimplifier() {
        return deafultSimplifier;
    }
    
    /**
     * Simplifies the given formula. Uses the previously set simplifier (see {@link #setSimplifier(Function)}).
     * 
     * @param formula The formla to simplify.
     * @return The simplified formula.
     */
    public static @NonNull Formula simplify(@NonNull Formula formula) {
        return notNull(simplifier.apply(formula));
    }
    
}
