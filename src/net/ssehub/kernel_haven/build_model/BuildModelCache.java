package net.ssehub.kernel_haven.build_model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.ssehub.kernel_haven.provider.AbstractCache;
import net.ssehub.kernel_haven.util.FormatException;
import net.ssehub.kernel_haven.util.io.csv.CsvReader;
import net.ssehub.kernel_haven.util.io.csv.CsvWriter;
import net.ssehub.kernel_haven.util.logic.Conjunction;
import net.ssehub.kernel_haven.util.logic.Disjunction;
import net.ssehub.kernel_haven.util.logic.False;
import net.ssehub.kernel_haven.util.logic.Formula;
import net.ssehub.kernel_haven.util.logic.Negation;
import net.ssehub.kernel_haven.util.logic.True;
import net.ssehub.kernel_haven.util.logic.Variable;
import net.ssehub.kernel_haven.util.logic.parser.CStyleBooleanGrammar;
import net.ssehub.kernel_haven.util.logic.parser.ExpressionFormatException;
import net.ssehub.kernel_haven.util.logic.parser.Parser;
import net.ssehub.kernel_haven.util.logic.parser.VariableCache;

/**
 * A cache for permanently saving (and reading) a build model to a (from a)
 * file.
 * 
 * @author Adam
 * @author Kevin
 */
public class BuildModelCache extends AbstractCache<BuildModel> {

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
    @Override
    public void write(BuildModel bm) throws IOException {
        try (CsvWriter writer = new CsvWriter(new FileOutputStream(cacheFile))) {
            for (File file : bm) {
                writer.writeRow(file.getPath(), bm.getPc(file).toString());
            }
        }
    }

    /**
     * Reads the BuildModel from the cache.
     * 
     * @param target Ignored.
     * 
     * @return The BuildModel or <code>null</code> if the cache is not present.
     * 
     * @throws FormatException
     *             if the cache is not valid.
     * @throws IOException
     *             Signals that an I/O exception has occurred. Possible Reasons:
     *             No ReadWrite Access File Already Exists
     */
    @Override
    public BuildModel read(File target) throws FormatException, IOException {
        CsvReader reader = null;
        BuildModel result = null;

        try {
            reader = new CsvReader(new FileInputStream(cacheFile));

            result = new BuildModel();

            VariableCache cache = new VariableCache();
            Parser<Formula> parser = new Parser<>(new CStyleBooleanGrammar(cache));

            String[] csvParts;
            while ((csvParts = reader.readNextRow()) != null) {
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
                result = True.INSTANCE;
            } else if (var.getName().equals("0")) {
                result = False.INSTANCE;
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
