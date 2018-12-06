package net.ssehub.kernel_haven.code_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;

/**
 * A superclass for all {@link CodeElement} that have nested elements.
 * 
 * @author Adam
 *
 * @param <NestedType> The type of the nested elements.
 */
public abstract class AbstractCodeElementWithNesting<NestedType extends CodeElement<NestedType>>
        extends AbstractCodeElement<NestedType> {

    private @NonNull List<@NonNull NestedType> nested;
    
    /**
     * Creates this element with the given presence condition. Source file is unknown, line numbers are -1 and
     * condition is <code>null</code>.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public AbstractCodeElementWithNesting(@NonNull Formula presenceCondition) {
        super(presenceCondition);
        
        this.nested = new LinkedList<>();
    }
    
    /**
     * De-serializes the given JSON to a {@link CodeElement}. This is the inverse operation to
     * {@link #serializeToJson(JsonObject, Function, Function)}.
     * 
     * @param json The JSON do de-serialize.
     * @param deserializeFunction The function to use for de-serializing secondary nested elements. Do not use this to
     *      de-serialize the {@link CodeElement}s in the primary nesting structure!
     *      (i.e. {@link #getNestedElement(int)})
     * 
     * @throws FormatException If the JSON does not have the expected format.
     */
    protected AbstractCodeElementWithNesting(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.nested = new LinkedList<>();
    }
    
    @Override
    public int getNestedElementCount() {
        return nested.size();
    }

    @Override
    public @NonNull NestedType getNestedElement(int index) throws IndexOutOfBoundsException {
        return notNull(nested.get(index));
    }

    @Override
    public void addNestedElement(@NonNull NestedType element) {
        nested.add(element);
    }
    
    /**
     * Replaces the given nested element with the given new element. This method should only be called by the extractor
     * that creates the AST.
     * 
     * @param oldElement The old element to replace.
     * @param newElement The new element to replace with.
     * 
     * @throws NoSuchElementException If oldElement is not nested inside this one.
     */
    protected void replaceNestedElement(@NonNull NestedType oldElement, @NonNull NestedType newElement)
            throws NoSuchElementException {
        
        int index = nested.indexOf(oldElement);
        if (index < 0) {
            throw new NoSuchElementException();
        }
        
        nested.set(index, newElement);
    }
    
    @Override
    public Iterator<@NonNull NestedType> iterator() {
        return nested.iterator();
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        int result = 1;

        for (NestedType n : nested) {
            result = 31 * result + hasher.hashCode((AbstractCodeElement<?>) n);
        }
        
        return result + super.hashCode(hasher);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof AbstractCodeElementWithNesting && super.equals(other, checker);
        
        if (equal) {
            AbstractCodeElementWithNesting<?> o = (AbstractCodeElementWithNesting<?>) other;
            
            equal = this.nested.size() == o.nested.size();
            for (int i = 0; equal && i < this.nested.size(); i++) {
                equal &= checker.isEqual((AbstractCodeElement<?>) this.nested.get(i),
                        (AbstractCodeElement<?>) o.nested.get(i));
            }
        }
        
        return equal;
    }

}
