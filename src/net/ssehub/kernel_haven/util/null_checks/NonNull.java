package net.ssehub.kernel_haven.util.null_checks;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies, that the annotated variable may <b>not</b> be <code>null</code>.
 * 
 * @author Adam
 */
@Retention(SOURCE)
@Target({ FIELD, METHOD, PARAMETER, LOCAL_VARIABLE })
public @interface NonNull {

}
