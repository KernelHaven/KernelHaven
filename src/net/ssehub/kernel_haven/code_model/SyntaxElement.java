package net.ssehub.kernel_haven.code_model;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.parser.Parser;

/**
 * An element of an AST representing the code.
 * 
 * @author Adam
 */
public class SyntaxElement implements CodeElement {

    private List<SyntaxElement> nested;
    
    private List<String> relations;
    
    private int lineStart;
    
    private int lineEnd;
    
    private File sourceFile;
    
    private Formula condition;
    
    private Formula presenceCondition;
    
    private ISyntaxElementType type;
    
    /**
     * Creates a new syntax element.
     * 
     * @param type The type of syntax element.
     * @param condition The immediate condition of this syntax element.
     * @param presencCondition The presence condition of this node.
     */
    public SyntaxElement(ISyntaxElementType type, Formula condition, Formula presencCondition) {
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
    public void setSourceFile(File sourceFile) {
        this.sourceFile = sourceFile;
    }
    
    @Override
    public int getNestedElementCount() {
        return nested.size();
    }

    @Override
    public SyntaxElement getNestedElement(int index) throws IndexOutOfBoundsException {
        return nested.get(index);
    }
    
    /**
     * Returns the first nested element with the given relation.
     * 
     * @param relation The relation of the nested element to this node.
     * @return The first nested element with the given relation. <code>null</code> if none found.
     */
    public SyntaxElement getNestedElement(String relation) {
        int i;
        for (i = 0; i < nested.size(); i++) {
            if (relations.get(i).equals(relation)) {
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
    public void addNestedElement(CodeElement element) {
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
    public void addNestedElement(SyntaxElement element, String relation) {
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
    public File getSourceFile() {
        return sourceFile;
    }

    @Override
    public Formula getCondition() {
        return condition;
    }

    @Override
    public Formula getPresenceCondition() {
        return presenceCondition;
    }
    
    /**
     * Returns the type of this AST node.
     * 
     * @return The type of syntax element.
     */
    public ISyntaxElementType getType() {
        return type;
    }
    
    /**
     * Returns the relation of the given nested syntax element to this element.
     * 
     * @param index The index of the nested element to get the relation for.
     * @return A string describing the relation of the nested element to this node.
     */
    public String getRelation(int index) {
        return relations.get(index);
    }
    
    /**
     * For deserialization: set the list of relations for the children. We first get this, and after this the
     * cache calls addNestedElement() a bunch of times. Package visibility; should only be used by
     * {@link SyntaxElementCsvUtil}.
     * 
     * @param relation The relations of the children elements.
     */
    void setDeserializedRelations(List<String> relation) {
        this.relations = relation;
    }
    
    @Override
    public List<String> serializeCsv() {
        return SyntaxElementCsvUtil.elementToCsv(this);
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
    public static SyntaxElement createFromCsv(String[] csv, Parser<Formula> parser) throws FormatException {
        return SyntaxElementCsvUtil.csvToElement(csv, parser);
    }

    /**
     * Iterates over the elements nested inside this element. Not recursively.
     * 
     * @return An iterable over the nested elements
     */
    public Iterable<SyntaxElement> iterateNestedSyntaxElements() {
        return new Iterable<SyntaxElement>() {
            
            @Override
            public Iterator<SyntaxElement> iterator() {
                return new Iterator<SyntaxElement>() {

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
    public String toString() {
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
    private String toString(String relation, String indentation) {
        StringBuilder result = new StringBuilder();
        
        String conditionStr = condition.toString();
        if (conditionStr.length() > 64) {
            conditionStr = "...";
        }
        
        result.append(indentation).append(relation).append(" [").append(conditionStr).append("] ");
        
        if (sourceFile != null) {
            result.append('[').append(sourceFile.getName()).append(':').append(lineStart).append("] ");
        }
        
        result.append(type.toString()).append('\n');
        
        indentation += '\t';
        
        for (int i = 0; i < nested.size(); i++) {
            result.append(nested.get(i).toString(relations.get(i), indentation));
        }
        
        return result.toString();
    }
    
}
