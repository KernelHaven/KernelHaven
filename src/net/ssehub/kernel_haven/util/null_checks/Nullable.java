package net.ssehub.kernel_haven.util.null_checks;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies, that the annotated variable may be <code>null</code>.
 * 
 * @author Adam
 */
@Retention(CLASS)
@Target({ TYPE_USE })
public @interface Nullable {

}
