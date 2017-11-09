package net.ssehub.kernel_haven.code_model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.FormulaCache;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.logic.parser.Parser;

/**
 * Utility functions for the CSV format of {@link SyntaxElement}s.
 *
 * @author Adam
 */
public class SyntaxElementCsvUtil {

    /**
     * Don't allow any instances.
     */
    private SyntaxElementCsvUtil() {
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
    public static SyntaxElement csvToElement(String[] csv, Parser<Formula> parser) throws FormatException {
        return csvToElement(csv, parser, null, null);
    }
    
    /**
     * Deserializes the given CSV into a element.
     * 
     * @param csv The csv.
     * @param parser The parser to parse boolean formulas.
     * @param formulaCache A cache for re-using already seen formulas.
     * @param filenameCache A cache for filename strings, that ensures that the same string objects are re-used.
     * @return The deserialized element.
     * 
     * @throws FormatException If the CSV is malformed.
     */
    public static SyntaxElement csvToElement(String[] csv, Parser<Formula> parser,
            Map<String, Formula> formulaCache, Map<String, File> filenameCache) throws FormatException {
        if (csv.length < 7) {
            throw new FormatException("Wrong number of CSV fields, expected at least 7 but got "
                    + csv.length + ": " + Arrays.toString(csv));
        }
        
        int lineStart;
        int lineEnd;
        try {
            lineStart = Integer.parseInt(csv[0]);
            lineEnd = Integer.parseInt(csv[1]);
        } catch (NumberFormatException e) {
            throw new FormatException(e);
        }
        
        File sourceFile = null;
        if (filenameCache != null) {
            sourceFile = filenameCache.get(csv[2]);
        }
        if (sourceFile == null) {
            sourceFile = new File(csv[2]);
            if (filenameCache != null) {
                filenameCache.put(csv[2], sourceFile);
            }
        }
        
        Formula condition;
        if (csv[3].equals("null")) {
            condition = null;
        } else {
            condition = readFormula(csv[3], parser, formulaCache);
        }
        Formula presenceConditon = readFormula(csv[4], parser, formulaCache);
        
        ISyntaxElementType type;
        String typeText = csv[5];
        if (typeText.startsWith("Literal: ")) {
            typeText = typeText.substring("Literal: ".length());
            type = new LiteralSyntaxElement(typeText);
        } else if (typeText.startsWith("Error: ")) {
            typeText = typeText.substring("Error: ".length());
            type = new ErrorSyntaxElement(typeText);
        } else {
            type = SyntaxElementTypes.getByName(typeText);
            if (type == null) {
                throw new FormatException("Unknown SyntaxElement type: " + typeText);
            }
        }
        
        int numRelations;
        try {
            numRelations = Integer.parseInt(csv[6]);
        } catch (NumberFormatException e) {
            throw new FormatException(e);
        }
        List<String> relation = new ArrayList<>(numRelations);
        for (int i = 7; i < 7 + numRelations; i++) {
            relation.add(csv[i]);
        }
        
        SyntaxElement result = new SyntaxElement(type, condition, presenceConditon);
        result.setLineStart(lineStart);
        result.setLineEnd(lineEnd);
        result.setSourceFile(sourceFile);
        result.setDeserializedRelations(relation);
        
        return result;
    }
    
    /**
     * Converts a syntax element into CSV.
     * 
     * @param element The element to convert.
     * @return The CSV.
     */
    public static List<String> elementToCsv(SyntaxElement element) {
        List<String> result = new ArrayList<>(7);
        
        String typeText;
        ISyntaxElementType type = element.getType();
        if (type instanceof LiteralSyntaxElement) {
            typeText = "Literal: " + ((LiteralSyntaxElement) type).getContent();
        } else if (type instanceof ErrorSyntaxElement) {
            typeText = "Error: " + ((ErrorSyntaxElement) type).getMessage();
        } else if (type instanceof SyntaxElementTypes) {
            typeText = type.toString();
        } else {
            // TODO: error handling
            typeText = "Error: Unknown type found in AST";
        }
        
        result.add(element.getLineStart() + "");
        result.add(element.getLineEnd() + "");
        result.add(element.getSourceFile().getPath());
        if (null != element.getCondition()) {
            StringBuffer formula = new StringBuffer();
            element.getCondition().toString(formula);
            result.add(formula.toString());
        } else {
            result.add("null");
        }
//        result.add(element.getCondition() == null ? "null" : element.getCondition().toString());
        StringBuffer formula = new StringBuffer();
        element.getPresenceCondition().toString(formula);
//        result.add(element.getPresenceCondition().toString());
        result.add(formula.toString());
        result.add(typeText);
        
        result.add(element.getNestedElementCount() + "");
        for (int i = 0; i < element.getNestedElementCount(); i++) {
            result.add(element.getRelation(i));
        }
        
        return result;
    }
    
    /**
     * Converts a syntax element into CSV.
     * 
     * @param element The element to convert.
     * @param cache A {@link FormulaCache} which may be used to cache already serialized formulas.
     * @return The CSV.
     */
    public static List<String> elementToCsv(SyntaxElement element, FormulaCache cache) {
        List<String> result = new ArrayList<>(7);
        
        String typeText;
        ISyntaxElementType type = element.getType();
        if (type instanceof LiteralSyntaxElement) {
            typeText = "Literal: " + ((LiteralSyntaxElement) type).getContent();
        } else if (type instanceof ErrorSyntaxElement) {
            typeText = "Error: " + ((ErrorSyntaxElement) type).getMessage();
        } else if (type instanceof SyntaxElementTypes) {
            typeText = type.toString();
        } else {
            // TODO: error handling
            typeText = "Error: Unknown type found in AST";
        }
        
        result.add(element.getLineStart() + "");
        result.add(element.getLineEnd() + "");
        result.add(element.getSourceFile().getPath());
        if (null != element.getCondition()) {
            result.add(cache.getSerializedFormula(element.getCondition()));
        } else {
            result.add("null");
        }
        result.add(cache.getSerializedFormula(element.getPresenceCondition()));
        result.add(typeText);
        
        result.add(element.getNestedElementCount() + "");
        for (int i = 0; i < element.getNestedElementCount(); i++) {
            result.add(element.getRelation(i));
        }
        
        return result;
    }
    
    /**
     * Converts a string into a formula. Optionally reads from cache first (and updates it, if cache miss).
     * 
     * @param text The string to parse into a formula.
     * @param parser The parser to use.
     * @param cache The cache to use. <code>null</code> if no cache required.
     * @return The string parsed to a formula.
     * 
     * @throws FormatException If parsing the string fails.
     */
    private static Formula readFormula(String text, Parser<Formula> parser, Map<String, Formula> cache)
            throws FormatException {
        
        Formula f = null;
        if (cache != null) {
            f = cache.get(text);
        }
        if (f == null) {
            try {
                f = parser.parse(text);
            } catch (ExpressionFormatException e) {
                throw new FormatException(e);
            }
            if (cache != null) {
                cache.put(text, f);
            }
        }
        return f;
    }
    
}
