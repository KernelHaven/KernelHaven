package net.ssehub.kernel_haven.util.io;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a class as a table-row compatible type. This means, that an instance of the class represents a single row
 * in a table-like structure. The single columns are specified via attributes inside this class annotated with the
 * {@link TableElement} annotation.
 *
 * @author Adam
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface TableRow {

}
