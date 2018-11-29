package net.ssehub.kernel_haven.code_model;

import static net.ssehub.kernel_haven.util.null_checks.NullHelpers.notNull;

import java.io.File;
import java.util.ArrayList;
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
public class CodeBlock extends AbstractCodeElementWithNesting<CodeBlock> {

    /**
     * Creates a new code block.
     * 
     * @param presenceCondition The presence condition of this block.
     */
    public CodeBlock(@NonNull Formula presenceCondition) {
        super(presenceCondition);
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
        
        super(presenceCondition);
        
        setSourceFile(sourceFile);
        setLineStart(lineStart);
        setLineEnd(lineEnd);
        setCondition(condition);
    }

    @Override
    public @NonNull List<@NonNull String> serializeCsv() {
        List<@NonNull String> result = new ArrayList<>(5);
        
        result.add(notNull(Integer.toString(getLineStart())));
        result.add(notNull(Integer.toString(getLineEnd())));
        result.add(notNull(getSourceFile().getPath()));
        Formula condition = getCondition();
        result.add(condition == null ? "null" : condition.toString());
        result.add(getPresenceCondition().toString());
        
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

    @Override
    public @NonNull String elementToString(@NonNull String indentation) {
        return "CodeBlock[start=" + getLineStart() + "; end=" + getLineEnd() + "; file=" + getSourceFile()
                + "; condition=" + getCondition() + "; pc=" + getPresenceCondition() + "]\n";
    }
    
}
