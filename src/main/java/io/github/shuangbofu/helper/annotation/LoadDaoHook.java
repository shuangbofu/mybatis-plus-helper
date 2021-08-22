package io.github.shuangbofu.helper.annotation;

import io.github.shuangbofu.helper.hook.DaoHook;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(LoadDaoHook.LoadDaoHooks.class)
@Inherited
public @interface LoadDaoHook {

    Class<? extends DaoHook<?>> value();

    boolean newInstance() default false;

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface LoadDaoHooks {
        LoadDaoHook[] value();
    }
}
