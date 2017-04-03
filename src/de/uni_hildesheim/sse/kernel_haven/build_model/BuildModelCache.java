package de.uni_hildesheim.sse.kernel_haven.build_model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import de.uni_hildesheim.sse.kernel_haven.util.FormatException;
import de.uni_hildesheim.sse.kernel_haven.util.Logger;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Conjunction;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Disjunction;
import de.uni_hildesheim.sse.kernel_haven.util.logic.False;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Formula;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Negation;
import de.uni_hildesheim.sse.kernel_haven.util.logic.True;
import de.uni_hildesheim.sse.kernel_haven.util.logic.Variable;
import de.uni_hildesheim.sse.kernel_haven.util.logic.parser.CStyleBooleanGrammar;
import de.uni_hildesheim.sse.kernel_haven.util.logic.parser.ExpressionFormatException;
import de.uni_hildesheim.sse.kernel_haven.util.logic.parser.Parser;
import de.uni_hildesheim.sse.kernel_haven.util.logic.parser.VariableCache;

/**
 * A cache for permanently saving (and reading) a build model to a (from a)
 * file.
 * 
 * @author Adam
 * @author Kevin
 */
public class BuildModelCache {

    public static final String CACHE_DELIMITER = ";";
    
    private static final Logger LOGGER = Logger.get();
    
    private File cacheFile;

    /**
     * Creates a new cache in the given cache directory.
     * 
     * @param cacheDir
     *            The directory where to store the cache files. This must be a
     *            directory, and we must be able to read and write to it.
     */
    public BuildModelCache(File cacheDir) {
        cacheFile = new File(cacheDir, "bmCache");
    }

    /**
     * Writes the BuildModel to the cache.
     *
     * @param bm
     *            the BuildModel to be written. Not Null.
     * @throws IOException
     *             Signals that an I/O exception has occurred. Possible Reasons:
     *             No ReadWrite Access File Already Exists
     */
    public void write(BuildModel bm) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(cacheFile));

            for (File file : bm) {
                writer.write(file.getPath() + CACHE_DELIMITER);
                writer.write(bm.getPc(file).toString());
                writer.write("\n");
            }
            LOGGER.logInfo("Build model cache successfully written");
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Reads the BuildModel from the cache.
     * 
     * @return The BuildModel or <code>null</code> if the cache is not present.
     * @throws FormatException
     *             if the cache is not valid.
     * @throws IOException
     *             Signals that an I/O exception has occurred. Possible Reasons:
     *             No ReadWrite Access File Already Exists
     */
    public BuildModel read() throws FormatException, IOException {
        BufferedReader reader = null;
        BuildModel result = null;

        try {
            reader = new BufferedReader(new FileReader(cacheFile));

            result = new BuildModel();

            VariableCache cache = new VariableCache();
            Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] csvParts = line.split(CACHE_DELIMITER);

                if (csvParts.length != 2) {
                    throw new FormatException("Invalid CSV");
                }

                File file = new File(csvParts[0]);

                Formula pc = null;
                try {
                    pc = parser.parse(csvParts[1]);

                } catch (ExpressionFormatException e) {
                    throw new FormatException(e);
                }

                cache.clear();

                pc = convertConstants(pc);
                result.add(file, pc);
                

            }
            LOGGER.logDebug("Cache reading of BM successful");

        } catch (FileNotFoundException e) {
            // ignore, so that null is returned if cache is not present

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {

                }
            }
        }
        
        if (result == null) {
            LOGGER.logDebug("BM Cache empty");
        }

        return result;
    }

    /**
     * Converts each variable "1" to true, and each "0" to false.
     * 
     * @param formula
     *            The formula to convert.
     * @return The converted formula.
     */
    private Formula convertConstants(Formula formula) {
        Formula result = null;

        if (formula instanceof Variable) {
            Variable var = (Variable) formula;
            if (var.getName().equals("1")) {
                result = new True();
            } else if (var.getName().equals("0")) {
                result = new False();
            } else {
                result = var;
            }

        } else if (formula instanceof Disjunction) {
            Disjunction dis = (Disjunction) formula;
            result = new Disjunction(convertConstants(dis.getLeft()), convertConstants(dis.getRight()));

        } else if (formula instanceof Conjunction) {
            Conjunction con = (Conjunction) formula;
            result = new Conjunction(convertConstants(con.getLeft()), convertConstants(con.getRight()));

        } else if (formula instanceof Negation) {
            Negation not = (Negation) formula;
            result = new Negation(convertConstants(not.getFormula()));

        } else {
            result = formula;

        }

        return result;
    }

}
