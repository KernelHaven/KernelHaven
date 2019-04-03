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
import net.ssehub.kernel_haven.util.null_checks.NonNull;
import net.ssehub.kernel_haven.util.null_checks.Nullable;

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
    public static @NonNull SyntaxElement csvToElement(@NonNull String @NonNull [] csv,
            @NonNull Parser<@NonNull Formula> parser) throws FormatException {
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
    public static @NonNull SyntaxElement csvToElement(@NonNull String @NonNull [] csv,
            @NonNull Parser<@NonNull Formula> parser, @Nullable Map<String, Formula> formulaCache,
            @Nullable Map<String, File> filenameCache) throws FormatException {
        
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
            typeText = notNull(typeText.substring("Literal: ".length()));
            type = new LiteralSyntaxElement(typeText);
        } else if (typeText.startsWith("Error: ")) {
            typeText = notNull(typeText.substring("Error: ".length()));
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
        List<@NonNull String> relation = new ArrayList<>(numRelations);
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
    public static @NonNull List<@NonNull String> elementToCsv(@NonNull SyntaxElement element) {
        return elementToCsv(element, null);
    }
    
    /**
     * Converts a syntax element into CSV.
     * 
     * @param element The element to convert.
     * @param cache A {@link FormulaCache} which may be used to cache already serialized presence conditions (formulas).
     *      May be <code>null</code>.
     * 
     * @return The CSV.
     */
    public static @NonNull List<@NonNull String> elementToCsv(@NonNull SyntaxElement element,
            @Nullable FormulaCache cache) {
        
        List<@NonNull String> result = new ArrayList<>(7);
        
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
        
        result.add(notNull(Integer.toString(element.getLineStart())));
        result.add(notNull(Integer.toString(element.getLineEnd())));
        result.add(notNull(element.getSourceFile().getPath()));
        
//      result.add(element.getCondition() == null ? "null" : element.getCondition().toString());
        Formula condition = element.getCondition();
        if (null != condition) {
            StringBuilder formula = new StringBuilder();
            condition.toString(formula);
            result.add(notNull(formula.toString()));
        } else {
            result.add("null");
        }
        
//        result.add(element.getPresenceCondition().toString());
        if (cache != null) {
            result.add(cache.getSerializedFormula(element.getPresenceCondition()));
        } else {
            StringBuilder formula = new StringBuilder();
            element.getPresenceCondition().toString(formula);
            result.add(notNull(formula.toString()));
        }
        
        
        result.add(typeText);
        
        result.add(notNull(Integer.toString(element.getNestedElementCount())));
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
    private static @NonNull Formula readFormula(@NonNull String text, @NonNull Parser<@NonNull Formula> parser,
            @Nullable Map<String, Formula> cache) throws FormatException {
        
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
