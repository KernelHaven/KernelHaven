package net.ssehub.kernel_haven.util;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.HashMap;
import java.util.Map;

import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * Caches transparently serialized formulas.<br/>
 * <b>Note:</b> This should work at best if for similarly formulas the <b>same</b> reference is used, because the
 * equality method can become very expensive for more complicated formulas.
 * @author El-Sharkawy
 *
 */
public class FormulaCache {
    
    private Map<Formula, String> cache = new HashMap<>();

    /**
     * Returns the cached serialized form of the specified formula. If it does not exist in the cache, it will cache it
     * transparently.<br/>
     * <b>Note:</b> This should work at best if for similarly formulas the <b>same</b> reference is used, because the
     * equality method can become very expensive for more complicated formulas.
     * @param formula The formula to serialize.
     * @return The serialized form ({@link Formula#toString()}).
     */
    public @NonNull String getSerializedFormula(@NonNull Formula formula) {
        String serialized = cache.get(formula);
        if (null == serialized) {
            // Serialize formula
            StringBuilder buffer = new StringBuilder();
            formula.toString(buffer);
            serialized = notNull(buffer.toString());
            
            // Cache result
            cache.put(formula, serialized);
        }
        
        return serialized;
    }
}
