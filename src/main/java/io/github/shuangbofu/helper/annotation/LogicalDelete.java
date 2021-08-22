package io.github.shuangbofu.helper.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface LogicalDelete {

    String name() default "status";

    String valid() default "1";

    String inValid() default "0";

    boolean active() default true;
}
