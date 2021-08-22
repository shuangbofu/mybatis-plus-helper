package io.github.shuangbofu.helper.config;

import io.github.shuangbofu.helper.dao.BaseDao;
import io.github.shuangbofu.helper.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassPathDaoScanner extends ClassPathBeanDefinitionScanner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathDaoScanner.class);
    private final ApplicationContext context;

    public ClassPathDaoScanner(BeanDefinitionRegistry registry, ApplicationContext context) {
        super(registry);
        this.context = context;
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        if (beanDefinitionHolders.isEmpty()) {
            LOGGER.warn("No dao was found in '{}' package, Please check your configuration!", (Object[]) basePackages);
        } else {
            LOGGER.info("Scan daos {}", beanDefinitionHolders.stream()
                    .map(BeanDefinitionHolder::getBeanName)
                    .collect(Collectors.toList()));
            processBeanDefinitions(beanDefinitionHolders);
        }
        return beanDefinitionHolders;
    }

    public void registerFilters() {
        addIncludeFilter(new AssignableTypeFilter(BaseDao.class) {
            @Override
            protected boolean matchClassName(String className) {
                return false;
            }
        });
        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            String className = metadataReader.getClassMetadata().getClassName();
            return className.endsWith("package-info");
        });
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        AbstractBeanDefinition definition;
        BeanDefinitionRegistry registry = getRegistry();
        for (BeanDefinitionHolder holder : beanDefinitions) {
            definition = (AbstractBeanDefinition) holder.getBeanDefinition();
            String beanClassName = definition.getBeanClassName();
            try {
                Class<?> beanClass = Class.forName(definition.getBeanClassName());
                Type[] genericTypes = ReflectUtils.getGenericTypes(beanClass);
                if (genericTypes.length > 1) {
                    Type actualTypeArgument = genericTypes[1];
                    ConstructorArgumentValues constructorArgumentValues = definition.getConstructorArgumentValues();
                    constructorArgumentValues.addIndexedArgumentValue(0, beanClassName);
                    constructorArgumentValues.addIndexedArgumentValue(1, actualTypeArgument);
                    constructorArgumentValues.addIndexedArgumentValue(2, context);
                    definition.setBeanClass(DaoFactoryBean.class);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (!definition.isSingleton()) {
                BeanDefinitionHolder proxyHolder = ScopedProxyUtils.createScopedProxy(holder, registry, true);
                if (registry.containsBeanDefinition(proxyHolder.getBeanName())) {
                    registry.removeBeanDefinition(proxyHolder.getBeanName());
                }
                registry.registerBeanDefinition(proxyHolder.getBeanName(), proxyHolder.getBeanDefinition());
            }
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            LOGGER.warn("Skipping MapperFactoryBean with name '" + beanName + "' and '"
                    + beanDefinition.getBeanClassName() + "' daoInterface" + ". Bean already defined with the same name!");
            return false;
        }
    }
}
