package net.ssehub.kernel_haven.util.null_checks;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies, that all variables inside the package are {@link NonNull} by default.
 * 
 * @author Adam
 */
@Retention(SOURCE)
@Target({ PACKAGE, TYPE, METHOD })
public @interface NonNullByDefault {

}
