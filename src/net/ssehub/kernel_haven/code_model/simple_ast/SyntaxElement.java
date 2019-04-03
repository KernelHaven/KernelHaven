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
package net.ssehub.kernel_haven.code_model.simple_ast;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.AbstractCodeElement;
import net.ssehub.kernel_haven.code_model.AbstractCodeElementWithNesting;
import net.ssehub.kernel_haven.code_model.CodeElement;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.FormulaCache;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * An element of an AST representing the code.
 * 
 * @author Adam
 */
public class SyntaxElement extends AbstractCodeElementWithNesting<SyntaxElement> {

    private @NonNull List<@NonNull String> relations;
    
    private @NonNull ISyntaxElementType type;
    
    /**
     * Creates a new syntax element.
     * 
     * @param type The type of syntax element.
     * @param condition The immediate condition of this syntax element.
     * @param presencCondition The presence condition of this node.
     */
    public SyntaxElement(@NonNull ISyntaxElementType type, @Nullable Formula condition,
            @NonNull Formula presencCondition) {
        
        super(presencCondition);
        setCondition(condition);
        
        this.relations = new LinkedList<>();
        this.type = type;
    }

    /**
     * Returns the first nested element with the given relation.
     * 
     * @param relation The relation of the nested element to this node.
     * @return The first nested element with the given relation. <code>null</code> if none found.
     */
    public @Nullable SyntaxElement getNestedElement(@NonNull String relation) {
        int i;
        for (i = 0; i < getNestedElementCount(); i++) {
            if (notNull(relations.get(i)).equals(relation)) {
                break;
            }
        }
        return i == getNestedElementCount() ? null : getNestedElement(i);
    }

    /**
     * Adds a nested element with an empty string as the relation. {@link #addNestedElement(SyntaxElement, String)}
     * should be used instead.
     */
    @Override
    public void addNestedElement(@NonNull SyntaxElement element) {
        super.addNestedElement(element);
        
        // if relations.size() >= nested.size(), then we already had a pre-defined relation for this lement
        // (e.g. from CSV deserializtaion).
        if (relations.size() < getNestedElementCount()) {
            relations.add("");
        }
    }
    
    /**
     * Adds a nested element with the given relation to this node.
     * 
     * @param element The element to nest inside of this.
     * @param relation The relation of the nested element.
     */
    public void addNestedElement(@NonNull SyntaxElement element, @NonNull String relation) {
        super.addNestedElement(element);
        this.relations.add(relation);
    }

    /**
     * Returns the type of this AST node.
     * 
     * @return The type of syntax element.
     */
    public @NonNull ISyntaxElementType getType() {
        return type;
    }
    
    /**
     * Returns the relation of the given nested syntax element to this element.
     * 
     * @param index The index of the nested element to get the relation for.
     * @return A string describing the relation of the nested element to this node.
     * 
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public @NonNull String getRelation(int index) throws IndexOutOfBoundsException {
        return notNull(relations.get(index));
    }
    
    /**
     * For deserialization: set the list of relations for the children. We first get this, and after this the
     * cache calls addNestedElement() a bunch of times. Package visibility; should only be used by
     * {@link SyntaxElementCsvUtil}.
     * 
     * @param relation The relations of the children elements.
     */
    void setDeserializedRelations(@NonNull List<@NonNull String> relation) {
        this.relations = relation;
    }
    
    @Override
    public @NonNull List<@NonNull String> serializeCsv() {
        return SyntaxElementCsvUtil.elementToCsv(this);
    }
    
    /**
     * Serializes this element, like {@link #serializeCsv()}, but uses a {@link FormulaCache} to reduce the overhead
     * while serializing formulas of this element.
     * TODO SE: @Adam I think a visitor would be much more beautiful.
     * @param cache The formula cache to cache the serialized formulas.
     * @return The CSV parts representing this element.
     */
    public @NonNull List<@NonNull String> serializeCsv(@Nullable FormulaCache cache) {
        return SyntaxElementCsvUtil.elementToCsv(this, cache);
    }
    
    /**
     * Deserializes the given CSV into a syntax element.
     * 
     * @param csv The csv.
     * @param parser The parser to parse boolean formulas.
     * @return The deserialized syntax element.
     * 
     * @throws FormatException If the CSV is malformed.
     */
    public static @NonNull SyntaxElement createFromCsv(@NonNull String @NonNull [] csv,
            @NonNull Parser<@NonNull Formula> parser) throws FormatException {
        
        return SyntaxElementCsvUtil.csvToElement(csv, parser);
    }

    @Override
    public @NonNull String toString() {
        // override this, since we need the relation handling
        return toString("", "");
    }
    
    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return type.toString() + '\n';
    }
    
    /**
     * Turns this node into a string with the given indentation. Recursively walks through its children with increased
     * indentation.
     * 
     * @param relation The relation of this node to its parent.
     * @param indentation The indentation. Contains only tabs. Never null.
     * 
     * @return This element as a string. Never null.
     */
    private @NonNull String toString(@NonNull String relation, @NonNull String indentation) {
        StringBuilder result = new StringBuilder();
        
        Formula condition = getCondition();
        String conditionStr = condition == null ? "<null>" : condition.toString();
        if (conditionStr.length() > 64) {
            conditionStr = "...";
        }
        
        result.append(indentation).append(relation).append(" [").append(conditionStr).append("] ");
        
        result.append('[').append(getSourceFile().getName()).append(':').append(getLineStart()).append("] ");
        
        result.append(type.toString()).append('\n');
        
        indentation += '\t';
        
        for (int i = 0; i < getNestedElementCount(); i++) {
            result.append(notNull(getNestedElement(i)).toString(relations.get(i), indentation));
        }
        
        return notNull(result.toString());
    }
    
    @Override
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return super.hashCode(hasher) + this.type.hashCode() + this.relations.hashCode();
    }
    
    @Override
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = other instanceof SyntaxElement && super.equals(other, checker);
        
        if (equal) {
            SyntaxElement o = (SyntaxElement) other;
            
            equal = o.type == this.type && o.relations.equals(this.relations);
        }
        
        return equal;
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        // TODO: JSON caching
        throw new UnsupportedOperationException("JSON caching not yet implementd for simple_ast.SyntaxElement");
    }
    
}
