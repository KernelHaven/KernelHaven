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
    
    /**
     * <p>
     * Specifies that this table row is of the special kind "relation". This means, that this table row has at least two
     * {@link TableElement}s (at index 0 and 1) <b>of the same type</b>. The two elements are in "relation" to each
     * other. This attribute only has an effect when turning this into an SQL table.
     * </p>
     * <p>
     * For example, consider the following relation objects:
     * <code>
     * <pre>
     * {"A", "B"}
     * {"A", "C"}
     * {"B", "C"}
     * </pre>
     * </code>
     * This would create the following two SQL tables:
     * <table>
     * <tr>
     *      <th>Name</th><th>ID</th>
     * </tr>
     * <tr>
     *      <td>A</td><td>1</td>
     * </tr>
     * <tr>
     *      <td>B</td><td>2</td>
     * </tr>
     * <tr>
     *      <td>C</td><td>3</td>
     * </tr>
     * </table>
     * <table>
     * <tr>
     *      <th>ID</th><th>ID</th>
     * </tr>
     * <tr>
     *      <td>1</td><td>2</td>
     * </tr>
     * <tr>
     *      <td>1</td><td>3</td>
     * </tr>
     * <tr>
     *      <td>2</td><td>3</td>
     * </tr>
     * </table>
     * </p>
     * 
     * <p>
     * Any other {@link TableElement}s than the first two (i.e. at index 2 or higher) are "relation parameters".
     * That means, the values are written as-is to the relation table (and not mapped to a separate ID table).
     * </p>
     * 
     * @return Whether this table row represents relations.
     */
    boolean isRelation() default false;

}
