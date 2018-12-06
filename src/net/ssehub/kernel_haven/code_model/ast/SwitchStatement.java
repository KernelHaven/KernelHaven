package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.AbstractCodeElement;
import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonList;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * <p>
 * Represents a switch statement. The nested children inside this element are the switch body. Statements after a
 * {@link CaseStatement} are nested inside that {@link CaseStatement}.
 * </p>
 * <p>
 * This class a list of all the {@link CaseStatement} that belong to this switch.
 * </p>
 *
 * @author Adam
 */
public class SwitchStatement extends AbstractSyntaxElementWithNesting {

    private @NonNull ICode header;
    
    private @NonNull List<@NonNull CaseStatement> cases;

    private @Nullable List<@NonNull Integer> serializationCasesIds;
    
    /**
     * Creates a {@link SwitchStatement}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param header The header of this switch.
     */
    public SwitchStatement(@NonNull Formula presenceCondition, @NonNull ICode header) {
        
        super(presenceCondition);
        this.header = header;
        this.cases = new LinkedList<>();
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
    protected SwitchStatement(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.header = (ICode) deserializeFunction.apply(json.getObject("switchCondition"));
        
        
        JsonList casesIds = json.getList("cases");
        List<@NonNull Integer> serializationCasesIds = new ArrayList<>(casesIds.getSize());
        for (JsonElement caseId : casesIds) {
            serializationCasesIds.add((Integer) ((JsonNumber) caseId).getValue());
        }
        
        this.serializationCasesIds = serializationCasesIds;
        this.cases = new LinkedList<>(); // will be filled in resolveIds()
    }
    
    /**
     * Returns the header of this switch.
     * 
     * @return The header of this switch.
     */
    public @NonNull ICode getHeader() {
        return header;
    }
    
    /**
     * Returns the {@link CaseStatement} at the given index.
     * 
     * @param index The index of the {@link CaseStatement} to get.
     * @return The {@link CaseStatement} at the given index.
     * 
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    public @NonNull CaseStatement getCase(int index) throws IndexOutOfBoundsException {
        return notNull(cases.get(index));
    }
    
    /**
     * Adds another {@link CaseStatement} to this switch. This should only be called by the extractors that
     * creates the AST. The {@link CaseStatement} should be added in the order they appear in the source file.
     * Each of the {@link CaseStatement}s added here should have a reference to this {@link SwitchStatement}.
     * 
     * @param caseStatement The {@link CaseStatement} to add.
     */
    public void addCase(@NonNull CaseStatement caseStatement) {
        cases.add(caseStatement);
    }
    
    /**
     * Returns the number of {@link CaseStatement}s that this switch has.
     * 
     * @return The number of {@link CaseStatement}s.
     */
    public int getCasesCount() {
        return cases.size();
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "Switch (" + getCasesCount() + " cases)\n" + header.toString(indentation + "\t");
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitSwitchStatement(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        int result = 1;
        
        for (CaseStatement sibling : cases) {
            result = 31 * result + hasher.hashCode(sibling);
        }
        
        return result + super.hashCode(hasher) + hasher.hashCode((AbstractCodeElement<?>) header);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof SwitchStatement && super.equals(other, checker);
        
        if (equal) {
            SwitchStatement o = (SwitchStatement) other;
            
            equal = checker.isEqual((AbstractCodeElement<?>) this.header, (AbstractCodeElement<?>) o.header);
            equal &= this.cases.size() == o.cases.size();
            
            for (int i = 0; equal && i < this.cases.size(); i++) {
                equal &= checker.isEqual(this.cases.get(i), o.cases.get(i));
            }
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("switchCondition", serializeFunction.apply(header));
        
        JsonList caseIds = new JsonList();
        for (CaseStatement element : cases) {
            caseIds.addElement(new JsonNumber(idFunction.apply(element)));
        }

        result.putElement("cases", caseIds);
    }
    
    @Override
    public void resolveIds(Map<Integer, CodeElement<?>> mapping) throws FormatException {
        super.resolveIds(mapping);
        
        List<@NonNull Integer> serializationCasesIds = this.serializationCasesIds;
        this.serializationCasesIds = null;
        if (serializationCasesIds == null) {
            throw new FormatException("Did not get de-erialization IDs");
        }
        
        for (Integer id : serializationCasesIds) {
            CaseStatement sibling = (CaseStatement) mapping.get(id);
            if (sibling == null) {
                throw new FormatException("Unknown ID: " + id);
            }
            this.cases.add(sibling);
        }
    }

}
