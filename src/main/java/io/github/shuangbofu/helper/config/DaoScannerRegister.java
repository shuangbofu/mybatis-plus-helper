package io.github.shuangbofu.helper.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import io.github.shuangbofu.helper.annotation.DaoScan;
import io.github.shuangbofu.helper.hook.SetTimeHook;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

public class DaoScannerRegister implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        AnnotationAttributes mapperScanAttrs = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(DaoScan.class.getName()));
        if (mapperScanAttrs != null) {
            String[] basePackages = mapperScanAttrs.getStringArray("basePackages");
            String basePackage = String.join(",", basePackages);
            BeanDefinition configurerDefine = BeanDefinitionBuilder.genericBeanDefinition(DaoScanConfigurer.class).addPropertyValue("basePackage", basePackage).getBeanDefinition();
            BeanDefinition setTimeHookDefine = BeanDefinitionBuilder.genericBeanDefinition(SetTimeHook.class).getBeanDefinition();
            BeanDefinition pluginDefine = BeanDefinitionBuilder.genericBeanDefinition(MybatisPlusInterceptor.class).getBeanDefinition();
            List<InnerInterceptor> interceptors = new ArrayList<>();
            interceptors.add(new PaginationInnerInterceptor(DbType.MYSQL));
            pluginDefine.getPropertyValues().addPropertyValue("interceptors", interceptors);
            registry.registerBeanDefinition("daoScanConfigurer", configurerDefine);
            registry.registerBeanDefinition("setTimeHook", setTimeHookDefine);
            registry.registerBeanDefinition("mybatisPlusInterceptor", pluginDefine);
        }
    }
}
