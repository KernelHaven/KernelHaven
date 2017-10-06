package net.ssehub.kernel_haven.variability_model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.ssehub.kernel_haven.util.FormatException;

/**
 * An Integer-based variability variable with a finite domain.
 * @author El-Sharkawy
 *
 */
public class FiniteIntegerVariable extends VariabilityVariable implements Iterable<Integer> {

    private int[] values;
    
    /**
     * Sole constructor for this class.
     * @param name The name of the new variable. Must not be <tt>null</tt>.
     * @param type The type of the new variable, e.g., <tt>integer</tt>. Must not be <tt>null</tt>.
     * @param values The allowed values for this variable.
     */
    public FiniteIntegerVariable(String name, String type, int[] values) {
        super(name, type);
        if (null != values) {
            Arrays.sort(values);
            this.values = values;
        } else {
            this.values = new int[0];
        }
    }

    /**
     * Returns the number of possible values for this variable.
     * @return A number &ge; 0.
     */
    public int getSizeOfRange() {
        return values.length;
    }
    
    /**
     * Returns the specified values.
     * @param index A 0-based index of the element to return. 
     * @return The element at the specified position in this list
     * 
     * @see #getSizeOfRange()
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<tt>index &lt; 0 || index &ge; {@link #getSizeOfRange()}</tt>)
     */
    public int getValue(int index) {
        return values[index];
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int pos = 0;

            @Override
            public boolean hasNext() {
                return values.length > pos;
            }

            @Override
            public Integer next() {
                return values[pos++];
            }
        };
    }
    
    @Override
    public List<String> serializeCsv() {
        List<String> result = super.serializeCsv();
        
        result.add(values.length + "");
        for (int value :  values) {
            result.add(value + "");
        }
        
        return result;
    }
    
    /**
     * Creates a {@link VariabilityVariable} from the given CSV.
     * 
     * @param csvParts
     *            The CSV that is converted into a {@link VariabilityVariable}.
     * @return The {@link VariabilityVariable} created by the CSV.
     * 
     * @throws FormatException
     *             If the CSV cannot be read into a variable.
     */
    public static VariabilityVariable createFromCsv(String[] csvParts) throws FormatException {
        VariabilityVariable variable = VariabilityVariable.createFromCsv(csvParts);
        try {
            int size = Integer.parseInt(csvParts[4]);
            int[] values = new int[size];
            
            for (int i = 0; i < size; i++) {
                values[i] = Integer.parseInt(csvParts[i + 5]);
            }
            
            FiniteIntegerVariable result = new FiniteIntegerVariable(variable.getName(), variable.getType(), values);
            
            return result;
        
        } catch (NumberFormatException e) {
            throw new FormatException(e);
        }
    }
    
}
