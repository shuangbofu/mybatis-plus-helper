package io.github.shuangbofu.helper.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface LogicalDelete {

    String name() default "deleted";

    String valid() default "0";

    String inValid() default "1";

    boolean active() default true;
}
