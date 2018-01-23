package net.ssehub.kernel_haven.code_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.FormulaCache;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * An element of an AST representing the code.
 * 
 * @author Adam
 */
public class SyntaxElement implements CodeElement {

    private @NonNull List<@NonNull SyntaxElement> nested;
    
    private @NonNull List<@NonNull String> relations;
    
    private int lineStart;
    
    private int lineEnd;
    
    private @NonNull File sourceFile;
    
    private @Nullable Formula condition;
    
    private @NonNull Formula presenceCondition;
    
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
        
        this.nested = new LinkedList<>();
        this.relations = new LinkedList<>();
        this.lineStart = -1;
        this.lineEnd = -1;
        this.sourceFile = new File("<unknown>");
        this.condition = condition;
        this.presenceCondition = presencCondition;
        this.type = type;
    }

    /**
     * Sets the line that this node starts on.
     * @param lineStart The start line of this node.
     */
    public void setLineStart(int lineStart) {
        this.lineStart = lineStart;
    }
    
    /**
     * Sets the line that this node ends on.
     * 
     * @param lineEnd The end line of this node.
     */
    public void setLineEnd(int lineEnd) {
        this.lineEnd = lineEnd;
    }
    
    /**
     * Sets the source file relative to the source tree that this node originates from.
     * 
     * @param sourceFile The source file. Not null.
     */
    public void setSourceFile(@NonNull File sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    @Override
    public int getNestedElementCount() {
        return nested.size();
    }

    @Override
    public @NonNull SyntaxElement getNestedElement(int index) throws IndexOutOfBoundsException {
        return notNull(nested.get(index));
    }
    
    /**
     * Returns the first nested element with the given relation.
     * 
     * @param relation The relation of the nested element to this node.
     * @return The first nested element with the given relation. <code>null</code> if none found.
     */
    public @Nullable SyntaxElement getNestedElement(@NonNull String relation) {
        int i;
        for (i = 0; i < nested.size(); i++) {
            if (notNull(relations.get(i)).equals(relation)) {
                break;
            }
        }
        return i == nested.size() ? null : nested.get(i);
    }

    /**
     * Adds a nested element with an empty string as the relation. {@link #addNestedElement(SyntaxElement, String)}
     * should be used instead.
     */
    @Override
    public void addNestedElement(@NonNull CodeElement element) {
        if (!(element instanceof SyntaxElement)) {
            throw new IllegalArgumentException("Can only add SyntaxElements as child of SyntaxElement");
        }
        nested.add((SyntaxElement) element);     
        
        // if relations.size() >= nested.size(), then we already had a pre-defined relation for this lement
        // (e.g. from CSV deserializtaion).
        if (relations.size() < nested.size()) {
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
        this.nested.add(element);
        this.relations.add(relation);
    }

    @Override
    public int getLineStart() {
        return lineStart;
    }

    @Override
    public int getLineEnd() {
        return lineEnd;
    }

    @Override
    public @NonNull File getSourceFile() {
        return sourceFile;
    }

    @Override
    public @Nullable Formula getCondition() {
        return condition;
    }

    @Override
    public @NonNull Formula getPresenceCondition() {
        return presenceCondition;
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

    /**
     * Iterates over the elements nested inside this element. Not recursively.
     * 
     * @return An iterable over the nested elements
     */
    public @NonNull Iterable<@NonNull SyntaxElement> iterateNestedSyntaxElements() {
        return new Iterable<@NonNull SyntaxElement>() {
            
            @Override
            public @NonNull Iterator<@NonNull SyntaxElement> iterator() {
                return new Iterator<@NonNull SyntaxElement>() {

                    private int index = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return index < getNestedElementCount();
                    }

                    @Override
                    public SyntaxElement next() {
                        return getNestedElement(index++);
                    }
                };
            }
        };
    }

    @Override
    public @NonNull String toString() {
        return toString("", "");
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
        
        Formula condition = this.condition;
        String conditionStr = condition == null ? "<null>" : condition.toString();
        if (conditionStr.length() > 64) {
            conditionStr = "...";
        }
        
        result.append(indentation).append(relation).append(" [").append(conditionStr).append("] ");
        
        result.append('[').append(sourceFile.getName()).append(':').append(lineStart).append("] ");
        
        result.append(type.toString()).append('\n');
        
        indentation += '\t';
        
        for (int i = 0; i < nested.size(); i++) {
            result.append(notNull(nested.get(i)).toString(relations.get(i), indentation));
        }
        
        return notNull(result.toString());
    }
    
}
