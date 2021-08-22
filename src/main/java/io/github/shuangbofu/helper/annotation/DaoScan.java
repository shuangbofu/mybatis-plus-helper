package io.github.shuangbofu.helper.annotation;

import io.github.shuangbofu.helper.config.DaoScannerRegister;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Configuration
@MapperScan
@Import(DaoScannerRegister.class)
public @interface DaoScan {
    String[] basePackages();

    @AliasFor(
            annotation = MapperScan.class,
            attribute = "basePackages")
    String[] mapperBasePackages();
}
