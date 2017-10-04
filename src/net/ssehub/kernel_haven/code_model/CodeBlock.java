package net.ssehub.kernel_haven.code_model;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.logic.parser.Parser;

/**
 * A block of one or multiple lines of code. For example, this could be used to represent an #ifdef hierarchy.
 * 
 * @author Adam
 */
public class CodeBlock implements CodeElement {

    private List<CodeBlock> nested;
    
    private int lineStart;
    
    private int lineEnd;
    
    private File sourceFile;
    
    private Formula condition;
    
    private Formula presenceCondition;
    
    /**
     * Creates a new code block.
     * 
     * @param presenceCondition The presence condition of this block.
     */
    public CodeBlock(Formula presenceCondition) {
        this.nested = new LinkedList<>();
        this.lineStart = -1;
        this.lineEnd = -1;
        this.sourceFile = new File("<unknown>");
        this.presenceCondition = presenceCondition;
    }
    
    /**
     * Creates a new code block.
     * 
     * @param lineStart The line where this block starts.
     * @param lineEnd The line where this block ends.
     * @param sourceFile The source file in the source tree where this block originates from.
     * @param condition The immediate condition of this block. May be <code>null</code>.
     * @param presenceCondition The presence condition of this block.
     */
    public CodeBlock(int lineStart, int lineEnd, File sourceFile, Formula condition, Formula presenceCondition) {
        this.nested = new LinkedList<>();
        this.lineStart = lineStart;
        this.lineEnd = lineEnd;
        this.sourceFile = sourceFile;
        this.condition = condition;
        this.presenceCondition = presenceCondition;
    }

    @Override
    public int getNestedElementCount() {
        return nested.size();
    }

    @Override
    public CodeBlock getNestedElement(int index) throws IndexOutOfBoundsException {
        return nested.get(index);
    }
    
    @Override
    public void addNestedElement(CodeElement element) {
        if (!(element instanceof CodeBlock)) {
            throw new IllegalArgumentException("Can only add CodeBlocks as child of CodeBlock");
        }
        nested.add((CodeBlock) element);
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

    @Override
    public List<String> serializeCsv() {
        List<String> result = new ArrayList<>(5);
        
        result.add(lineStart + "");
        result.add(lineEnd + "");
        result.add(sourceFile.getPath());
        result.add(condition == null ? "null" : condition.toString());
        result.add(presenceCondition.toString());
        
        return result;
    }
    
    /**
     * Deserializes the given CSV into a block.
     * 
     * @param csv The csv.
     * @param parser The parser to parse boolean formulas.
     * @return The deserialized block.
     * 
     * @throws FormatException If the CSV is malformed.
     */
    public static CodeBlock createFromCsv(String[] csv, Parser<Formula> parser) throws FormatException {
        if (csv.length != 5) {
            throw new FormatException("Invalid CSV");
        }
        
        int lineStart = Integer.parseInt(csv[0]);
        int lineEnd = Integer.parseInt(csv[1]);
        File sourceFile = new File(csv[2]);
        Formula condition = null;
        if (!csv[3].equals("null")) {
            try {
                condition = parser.parse(csv[3]);
            } catch (ExpressionFormatException e) {
                throw new FormatException(e);
            }
        }
        
        Formula presenceCondition;
        try {
            presenceCondition = parser.parse(csv[4]);
        } catch (ExpressionFormatException e) {
            throw new FormatException(e);
        }
        
        return new CodeBlock(lineStart, lineEnd, sourceFile, condition, presenceCondition);
    }

    /**
     * Iterates over the blocks nested inside this blocks. Not recursively.
     * 
     * @return An iterable over the nested blocks.
     */
    public Iterable<CodeBlock> iterateNestedBlocks() {
        return new Iterable<CodeBlock>() {
            
            @Override
            public Iterator<CodeBlock> iterator() {
                return new Iterator<CodeBlock>() {

                    private int index = 0;
                    
                    @Override
                    public boolean hasNext() {
                        return index < getNestedElementCount();
                    }

                    @Override
                    public CodeBlock next() {
                        return getNestedElement(index++);
                    }
                };
            }
        };
    }
    
}
