package io.github.shuangbofu.helper.config;

import io.github.shuangbofu.helper.annotation.DaoScan;
import io.github.shuangbofu.helper.hook.SetTimeHook;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

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
            registry.registerBeanDefinition("daoScanConfigurer", configurerDefine);
            registry.registerBeanDefinition("setTimeHook", setTimeHookDefine);
        }
    }
}
