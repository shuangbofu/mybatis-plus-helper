package io.github.shuangbofu.helper.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;

import static org.springframework.util.Assert.notNull;

public class DaoScanConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, EmbeddedValueResolverAware {
    private String basePackage;
    private boolean processPropertyPlaceHolders;
    private ApplicationContext applicationContext;
    private StringValueResolver resolver;

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (!processPropertyPlaceHolders) {
            // set
        }
        ClassPathDaoScanner scanner = new ClassPathDaoScanner(registry, applicationContext);
        scanner.registerFilters();
        scanner.setResourceLoader(applicationContext);
        scanner.scan(basePackage.split(","));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(basePackage, "Property 'basePackage' is required");
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }
}
