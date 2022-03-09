package helper.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An order definition of fields in a class.
 *
 * @author errorfatal89@gmail.com
 * @date 2021/12/28
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SerializerOrder {
    int value() default 1;
}
