/*
 * Copyright 2017-2019 University of Hildesheim, Software Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.ssehub.kernel_haven.code_model.ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.Map;
import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.AbstractCodeElement;
import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Represents a <tt>case</tt> or a <tt>default</tt> block of a switch statement. The children nested inside this are
 * the content of the case.
 * 
 * TODO SE: should content following a case: statement really be nested inside of it? 
 * 
 * @author El-Sharkawy
 */
public class CaseStatement extends AbstractSyntaxElementWithNesting {
    
    /**
     * The type of case or default statement.
     */
    public static enum CaseType {
        CASE, DEFAULT;
    }

    private @Nullable ICode caseCondition;
    
    private @NonNull CaseType type;
    
    private @NonNull SwitchStatement switchStatement;
    
    private @Nullable Integer serializationSwitchId;
    
    /**
     * Creates a {@link CaseStatement}.
     * 
     * @param presenceCondition The presence condition of this element.
     * @param caseCondition The value of a case statement; <code>null</code> if this is a default statement.
     * @param type The {@link CaseType} of statement that this is.
     * @param switchStatement The {@link SwitchStatement} to which this case belongs to.
     */
    public CaseStatement(@NonNull Formula presenceCondition, @Nullable ICode caseCondition,
            @NonNull CaseType type, @NonNull SwitchStatement switchStatement) {
        
        super(presenceCondition);
        this.caseCondition = caseCondition;
        this.type = type;
        this.switchStatement = switchStatement;
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
    @SuppressWarnings("null") // switchStatement will be initialized in resolveIds()
    protected CaseStatement(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        super(json, deserializeFunction);
        
        this.type = CaseType.valueOf(json.getString("caseType"));
        if (json.getElement("caseCondition") != null) {
            this.caseCondition = (ICode) deserializeFunction.apply(json.getObject("caseCondition"));
        }
        
        this.serializationSwitchId = json.getInt("switch");
    }
    
    /**
     * Returns the value of a case statement; <code>null</code> if this is a default statement.
     * 
     * @return The value of a case statement.
     */
    public @Nullable ICode getCaseCondition() {
        return caseCondition;
    }
    
    /**
     * Returns the {@link CaseType} of statement that this is.
     * 
     * @return The {@link CaseType} of statement that this is.
     */
    public @NonNull CaseType getType() {
        return type;
    }
    
    /**
     * Returns the {@link SwitchStatement} that this case belongs to.
     * 
     * @return The {@link SwitchStatement} that this {@link CaseStatement} belongs to.
     */
    public @NonNull SwitchStatement getSwitchStatement() {
        return switchStatement;
    }

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        ICode caseCondition = this.caseCondition;
        
        String result = type.name() + "\n";
        if (caseCondition != null) {
            result += caseCondition.toString(indentation + "\t");
        }
        
        return result;
    }

    @Override
    public void accept(@NonNull ISyntaxElementVisitor visitor) {
        visitor.visitCaseStatement(this);
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + type.hashCode() + hasher.hashCode(switchStatement)
                + (caseCondition != null ? hasher.hashCode((AbstractCodeElement<?>) caseCondition) : 523);
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof CaseStatement && super.equals(other, checker);
        
        if (equal) {
            CaseStatement o = (CaseStatement) other;
            
            if (this.caseCondition != null && o.caseCondition != null) {
                equal = this.type == o.type && checker.isEqual(
                        (AbstractCodeElement<?>) this.caseCondition, (AbstractCodeElement<?>) o.caseCondition);
            } else {
                equal = this.type == o.type && this.caseCondition == o.caseCondition;
            }
            
            equal &= checker.isEqual(this.switchStatement, o.switchStatement);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        super.serializeToJson(result, serializeFunction, idFunction);
        
        result.putElement("caseType", new JsonString(notNull(type.name())));
        if (caseCondition != null) {
            result.putElement("caseCondition", serializeFunction.apply(caseCondition));
        }
        result.putElement("switch", new JsonNumber(idFunction.apply(switchStatement)));
    }
    
    @Override
    public void resolveIds(Map<Integer, CodeElement<?>> mapping) throws FormatException {
        super.resolveIds(mapping);
        
        Integer serializationSwitchId = this.serializationSwitchId;
        this.serializationSwitchId = null;
        if (serializationSwitchId == null) {
            throw new FormatException("Did not get ID");
        }
        
        SwitchStatement stmt = (SwitchStatement) mapping.get(serializationSwitchId);
        if (stmt == null) {
            throw new FormatException("Unkown ID: " + serializationSwitchId);
        }
        this.switchStatement = stmt;
    }

}
