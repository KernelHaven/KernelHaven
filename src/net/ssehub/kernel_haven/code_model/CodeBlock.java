package net.ssehub.kernel_haven.code_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

/**
 * A block of one or multiple lines of code. For example, this could be used to represent an #ifdef hierarchy.
 * 
 * @author Adam
 */
public class CodeBlock implements CodeElement {

    private @NonNull List<@NonNull CodeBlock> nested;
    
    private int lineStart;
    
    private int lineEnd;
    
    private @NonNull File sourceFile;
    
    private @Nullable Formula condition;
    
    private @NonNull Formula presenceCondition;
    
    /**
     * Creates a new code block.
     * 
     * @param presenceCondition The presence condition of this block.
     */
    public CodeBlock(@NonNull Formula presenceCondition) {
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
    public CodeBlock(int lineStart, int lineEnd, @NonNull File sourceFile, @Nullable Formula condition,
            @NonNull Formula presenceCondition) {
        
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
    public @NonNull CodeBlock getNestedElement(int index) throws IndexOutOfBoundsException {
        return notNull(nested.get(index));
    }
    
    @Override
    public void addNestedElement(@NonNull CodeElement element) {
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

    @Override
    public @NonNull List<@NonNull String> serializeCsv() {
        List<@NonNull String> result = new ArrayList<>(5);
        
        result.add(notNull(Integer.toString(lineStart)));
        result.add(notNull(Integer.toString(lineEnd)));
        result.add(notNull(sourceFile.getPath()));
        Formula condition = this.condition;
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
    public static @NonNull CodeBlock createFromCsv(@NonNull String @NonNull [] csv,
            @NonNull Parser<@NonNull Formula> parser) throws FormatException {
        
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
    public @NonNull Iterable<@NonNull CodeBlock> iterateNestedBlocks() {
        return new Iterable<@NonNull CodeBlock>() {
            
            @Override
            public @NonNull Iterator<@NonNull CodeBlock> iterator() {
                return new Iterator<@NonNull CodeBlock>() {

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
    
    @Override
    public int hashCode() {
        return Integer.hashCode(lineStart) + Integer.hashCode(lineEnd) + sourceFile.hashCode()
            + (condition != null ? condition.hashCode() : 54234) + presenceCondition.hashCode()
            + nested.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        boolean equal = false;
        if (obj instanceof CodeBlock) {
            CodeBlock other = (CodeBlock) obj;
            equal = true;
            
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
            
            if (equal) {
                equal &= this.nested.equals(other.nested);
            }
        }
        return equal;
    }
    
    @Override
    public String toString() {
        return "CodeBlock[start=" + lineStart + "; end=" + lineEnd + "; file=" + sourceFile + "; condition="
                + condition + "; pc=" + presenceCondition + "; #children=" + nested.size() + "]";
    }

    @Override
    public void setLineStart(int start) {
        this.lineStart = start;
    }

    @Override
    public void setLineEnd(int end) {
        this.lineEnd = end;
    }
    
}
