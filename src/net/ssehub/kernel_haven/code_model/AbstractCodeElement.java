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
package net.ssehub.kernel_haven.code_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import net.ssehub.kernel_haven.code_model.JsonCodeModelCache.CheckedFunction;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.json.JsonElement;
import net.ssehub.kernel_haven.util.io.json.JsonNumber;
import net.ssehub.kernel_haven.util.io.json.JsonObject;
import net.ssehub.kernel_haven.util.io.json.JsonString;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.parser.CStyleBooleanGrammar;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.logic.parser.VariableCache;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * Abstract superclass for all {@link CodeElement}s. This implementation of the {@link CodeElement} interface has
 * no support for nested elements. Use {@link AbstractCodeElementWithNesting} for that.
 * 
 * @param <NestedType> The type of the nested elements.
 * 
 * @author Adam
 */
public abstract class AbstractCodeElement<NestedType extends CodeElement<NestedType>>
        implements CodeElement<NestedType> {

    private static final @NonNull Parser<@NonNull Formula> PARSER
            = new Parser<>(new CStyleBooleanGrammar(new VariableCache()));
    
    private static final @NonNull File UNKNOWN = new File("<unknown>");
    
    private @NonNull File sourceFile;
    
    private int lineStart;
    
    private int lineEnd;
    
    private @Nullable Formula condition;
    
    private @NonNull Formula presenceCondition;
    
    /**
     * Creates this element with the given presence condition. Source file is unknown, line numbers are -1 and
     * condition is <code>null</code>.
     * 
     * @param presenceCondition The presence condition of this element.
     */
    public AbstractCodeElement(@NonNull Formula presenceCondition) {
        this.sourceFile = UNKNOWN;
        this.lineStart = -1;
        this.lineEnd = -1;
        this.condition = null;
        this.presenceCondition = presenceCondition;
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
    protected AbstractCodeElement(@NonNull JsonObject json,
        @NonNull CheckedFunction<@NonNull JsonElement, @NonNull CodeElement<?>, FormatException> deserializeFunction)
        throws FormatException {
        
        this.sourceFile = new File(json.getString("sourceFile"));
        this.lineStart = json.getInt("lineStart");
        this.lineEnd = json.getInt("lineEnd");

        if (json.getElement("condition") != null) {
            this.condition = parseJsonFormula(json.getString("condition"));
        }
        
        this.presenceCondition = parseJsonFormula(json.getString("presenceCondition"));
    }
    
    /**
     * Utility method for de-serialization. Parses the given formula string to a formula.
     * 
     * @param formula The formula string to parse.
     * 
     * @return The parsed formula.
     * 
     * @throws FormatException If the formula can't be parsed.
     */
    protected @NonNull Formula parseJsonFormula(@NonNull String formula) throws FormatException {
        try {
            return PARSER.parse(formula);
        } catch (ExpressionFormatException e) {
            throw new FormatException("Can't parse formula", e);
        }
    }

    @Override
    public @NonNull File getSourceFile() {
        return sourceFile;
    }

    /**
     * Changes the source file.
     * 
     * @param sourceFile The new source file.
     * 
     * @see #getSourceFile()
     */
    public void setSourceFile(@NonNull File sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    @Override
    public int getLineStart() {
        return lineStart;
    }

    @Override
    public void setLineStart(int start) {
        this.lineStart = start;
    }

    @Override
    public int getLineEnd() {
        return lineEnd;
    }

    @Override
    public void setLineEnd(int end) {
        this.lineEnd = end;
    }
    
    @Override
    public @Nullable Formula getCondition() {
        return condition;
    }
    
    /**
     * Changes the immediate condition of this element.
     * 
     * @param condition The new condition.
     * 
     * @see #getCondition()
     */
    protected void setCondition(Formula condition) {
        this.condition = condition;
    }

    @Override
    public @NonNull Formula getPresenceCondition() {
        return presenceCondition;
    }
    
    /**
     * Changes the presence condition of this element.
     * 
     * @param presenceCondition The new presence condition.
     * 
     * @see #getPresenceCondition()
     */
    protected void setPresenceCondition(@NonNull Formula presenceCondition) {
        this.presenceCondition = presenceCondition;
    }

    @Override
    public int getNestedElementCount() {
        return 0;
    }

    @Override
    public @NonNull NestedType getNestedElement(int index) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public void addNestedElement(@NonNull NestedType element) throws IndexOutOfBoundsException {
        throw new IndexOutOfBoundsException();
    }
    
    @Override
    public Iterator<@NonNull NestedType> iterator() {
        return new Iterator<@NonNull NestedType>() {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public @NonNull NestedType next() {
                throw new NoSuchElementException();
            }
            
        };
    }
    
    @Override
    public @NonNull String toString() {
        return toString("");
    }
    
    @Override
    public @NonNull String toString(@NonNull String indentation) {
        StringBuilder result = new StringBuilder();
        
        Formula condition = getCondition();
        String conditionStr = condition == null ? "<null>" : condition.toString();
        if (conditionStr.length() > 64) {
            conditionStr = "...";
        }
        
        result.append(indentation).append("[").append(conditionStr).append("] ");
        
        result.append(elementToString(indentation));
        
        indentation += '\t';
        
        for (NestedType child : this) {
            result.append(child.toString(indentation));
        }
        
        return notNull(result.toString());
    }
    
    /**
     * A pair of two {@link AbstractCodeElement}s. This helper class allows using two {@link AbstractCodeElement}s
     * as keys in a set or map. The order of the two {@link AbstractCodeElement}s does not matter. The
     * {@link #equals(Object)} method checks on object identity of the two elements, without considering order.
     */
    private static final class Pair {
        
        private @NonNull AbstractCodeElement<?> c1;
        
        private @NonNull AbstractCodeElement<?> c2;

        /**
         * Creates a pair of the two given {@link AbstractCodeElement}.
         * 
         * @param c1 The first element.
         * @param c2 The second element.
         */
        public Pair(@NonNull AbstractCodeElement<?> c1, @NonNull AbstractCodeElement<?> c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        @Override
        public int hashCode() {
            // symmetric for switched c1 and c2
            return System.identityHashCode(c1) * System.identityHashCode(c2);
        }
        
        @Override
        public boolean equals(Object obj) {
            boolean equal = false;
            
            if (obj instanceof Pair) {
                Pair other = (Pair) obj;
                
                // symmetric for switched c1 and c2
                equal = (other.c1 == this.c1 && other.c2 == this.c2) || (other.c1 == this.c2 && other.c2 == this.c1);
            }
            
            return equal;
        }
        
        @Override
        public String toString() {
            return Integer.toHexString(c1.hashCode()) + "x" + Integer.toHexString(c2.hashCode());
        }
        
    }
    
    /**
     * A helper class for checking equality of two {@link AbstractCodeElement}s. This is needed since cross-references
     * may lead to stack overflows with the trivial implementation of equals(). This class keeps a map of already
     * checked pairs, and also ensures that no endless recursion occurs. (the latter is done by assuming that two
     * elements, that are currently being compared, are equal if they are encountered deeper down in the tree, again).
     */
    protected static class CodeElementEqualityChecker {

        private @NonNull Map<Pair, Boolean> visited = new HashMap<>();
        
        private @NonNull Set<@NonNull Pair> currentlyVisiting = new HashSet<>();
        
        /**
         * Checks if the two {@link AbstractCodeElement}s are equal.
         * 
         * @param c1 The first code element.
         * @param c2 The second code element.
         * 
         * @return Whether the two elements are equal.
         */
        public boolean isEqual(@NonNull AbstractCodeElement<?> c1, @NonNull AbstractCodeElement<?> c2) {
            Boolean result;

            Pair p = new Pair(c1, c2);
            
            if (c1 == c2)  {
                result = true;
                
            } else if (currentlyVisiting.contains(p)) {
                result = true; // assume true if any parent is currently visiting this pair
                
            } else {
                result = visited.get(p);
                if (result == null) {
                    
                    currentlyVisiting.add(p);
                    result = c1.equals(c2, this);
                    currentlyVisiting.remove(p);
                    
                    visited.put(p, result);
                }
            }
            
            return result;
        }
        
    }
    
    /**
     * Checks if the this element equals the given other element. Equality checks on nested {@link CodeElement}s
     * should use the {@link CodeElementEqualityChecker#isEqual(AbstractCodeElement, AbstractCodeElement)} method.
     * Overriding methods should always call the super method of this.
     * 
     * @param other The other element that needs to be checked.
     * @param checker The checker to use for equality checks of nested elements.
     * 
     * @return Whether this element equals the given other element.
     */
    protected boolean equals(@NonNull AbstractCodeElement<?> other, @NonNull CodeElementEqualityChecker checker) {
        boolean equal = false;
        
        equal = this.lineStart == other.lineStart && this.lineEnd == other.lineEnd;
        if (equal) {
            equal &= this.sourceFile.equals(other.sourceFile);
        }
        
        if (equal) {
            Formula condition = this.condition;
            if (condition == null) {
                equal &= other.condition == null;
            } else {
                equal &= condition.equals(other.condition);
            }
        }
        
        if (equal) {
            equal &= this.presenceCondition.equals(other.presenceCondition);
        }
        
        return equal;
    }

    // this method is mainly implemented for test cases
    @Override
    public final boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof AbstractCodeElement) {
            equal = new CodeElementEqualityChecker().isEqual(this, (AbstractCodeElement<?>) obj);
        }
        return equal;
    }
    
    /**
     * A helper class for calculating hashCode() of {@link AbstractCodeElement}s. This is needed since cross-references
     * may lead to stack overflows with the trivial implementation of hashCode(). This class keeps a map of already
     * hashed elements, and also ensures that no endless recursion occurs. (the latter is done by returning 0 as the
     * hash for an element that is currently being hashed, if they it is encountered deeper down in the tree, again).
     */
    protected static class CodeElementHasher {
        
        private @NonNull Map<IdentityWrapper<CodeElement<?>>, Integer> visited = new HashMap<>();
        
        private @NonNull Set<@NonNull IdentityWrapper<CodeElement<?>>> currentlyVisiting = new HashSet<>();
        
        /**
         * Hashes the given {@link AbstractCodeElement}.
         * 
         * @param element the element to hash.
         * 
         * @return The hash of the given element.
         */
        public int hashCode(@NonNull AbstractCodeElement<?> element) {
            Integer result;

            IdentityWrapper<CodeElement<?>> wrap = new IdentityWrapper<CodeElement<?>>(element);
            
            if (currentlyVisiting.contains(wrap)) {
                result = 0; // return 0 if a higher calling method is currently evaluating this element
                
            } else {
                result = visited.get(wrap);
                if (result == null) {
                    currentlyVisiting.add(wrap);
                    result = element.hashCode(this);
                    currentlyVisiting.remove(wrap);
                    
                    visited.put(wrap, result);
                }
            }
            
            return result;
        }
        
    }
    
    /**
     * Calculates a hash for this element. Hash calculations of nested elements should use the
     * {@link CodeElementHasher#hashCode(AbstractCodeElement)} method. Overriding methods should always call the
     * super method of this.
     * 
     * @param hasher The hasher to use for nested elements.
     * 
     * @return The hash of this element.
     */
    protected int hashCode(@NonNull CodeElementHasher hasher) {
        return Integer.hashCode(lineStart) + Integer.hashCode(lineEnd) + sourceFile.hashCode()
            + (condition != null ? condition.hashCode() : 54234) + presenceCondition.hashCode();
    }
    
    // this method is mainly implemented for test cases
    @Override
    public final int hashCode() {
        return new CodeElementHasher().hashCode(this);
    }
    
    @Override
    public void serializeToJson(JsonObject result,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull JsonElement> serializeFunction,
            @NonNull Function<@NonNull CodeElement<?>, @NonNull Integer> idFunction) {
        
        result.putElement("sourceFile", new JsonString(notNull(sourceFile.getPath().replace(File.separatorChar, '/'))));
        result.putElement("lineStart", new JsonNumber(lineStart));
        result.putElement("lineEnd", new JsonNumber(lineEnd));
        
        if (condition != null) {
            result.putElement("condition", new JsonString(condition.toString()));
        }
        
        result.putElement("presenceCondition", new JsonString(presenceCondition.toString()));
    }
    
    @Override
    public void resolveIds(Map<Integer, CodeElement<?>> mapping) throws FormatException {
        // do nothing
    }
    
}
