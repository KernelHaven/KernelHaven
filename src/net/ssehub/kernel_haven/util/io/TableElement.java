package net.ssehub.kernel_haven.util.io;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks an attribute inside a class marked via the {@link TableRow} as a single column element. The
 * {@link #toString()} method of this attribute will be used to fill the field in the table.
 *
 * @author Adam
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface TableElement {
    
    /**
     * Defines the position of this column in the table. The first column has the index 0.
     * 
     * @return The index of the column that this field should be placed in. Must not be negative. All fields in a given
     *      {@link TableRow} class must form a coherent interval, starting from 0.
     */
    int index();
    
    /**
     * The name of this field.
     * 
     * @return The name of this field, to be used in the table header.
     */
    String name();

}
