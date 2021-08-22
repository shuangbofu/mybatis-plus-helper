package io.github.shuangbofu.helper.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.shuangbofu.helper.annotation.LoadDaoHook;
import io.github.shuangbofu.helper.annotation.LogicalDelete;
import io.github.shuangbofu.helper.dao.AbstractDao;
import io.github.shuangbofu.helper.dao.BaseDao;
import io.github.shuangbofu.helper.dao.LogicalDao;
import io.github.shuangbofu.helper.entity.IdEntity;
import io.github.shuangbofu.helper.hook.DaoHook;
import io.github.shuangbofu.helper.proxy.DaoProxy;
import io.github.shuangbofu.helper.proxy.DaoProxyFactory;
import io.github.shuangbofu.helper.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DaoFactoryBean<ENTITY extends IdEntity, MAPPER extends BaseMapper<ENTITY>, DAO extends BaseDao<ENTITY, MAPPER>> implements FactoryBean<DAO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaoFactoryBean.class);

    static {
        ReflectUtils.disableWarning();
    }

    private final Map<Class<?>, DaoHook<ENTITY>> instanceCache = new HashMap<>();
    private final Class<DAO> daoInterface;
    private final Class<MAPPER> mapperClass;
    private final ApplicationContext context;

    public DaoFactoryBean(Class<DAO> daoInterface, Class<MAPPER> mapperClass, ApplicationContext context) {
        this.daoInterface = daoInterface;
        this.context = context;
        this.mapperClass = mapperClass;
    }

    @Override
    public DAO getObject() throws Exception {
        MAPPER bean;
        try {
            bean = context.getBean(mapperClass);
            LOGGER.info("Get mapper bean {} from spring", mapperClass.getName());
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Not found mapper [%s] bean", mapperClass.getName()), e);
        }
        return new DaoProxyFactory<>(daoInterface).newInstance(getProxy(bean));
    }

    @Override
    public Class<?> getObjectType() {
        return daoInterface;
    }

    private DaoProxy<ENTITY, MAPPER> getProxy(MAPPER mapper) {
        Type[] types = ReflectUtils.getGenericTypes(mapperClass);
        if (types.length == 0) {
            throw new IllegalArgumentException("Not found mapper generic types ");
        }
        Class<?> modalClass = (Class<?>) types[0];
        // 逻辑删除
        LogicalDelete logicalDelete = modalClass.getAnnotation(LogicalDelete.class);
        List<LoadDaoHook> hookAnnotations = ReflectUtils.getAnnotations(modalClass, LoadDaoHook.class);
        // 所有回调接口
        List<DaoHook<ENTITY>> hooks = hookAnnotations.stream()
                .map(this::getHooKOptional)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinct()
                .collect(Collectors.toList());
        return new DaoProxy<>(
                logicalDelete != null && logicalDelete.active() ?
                        new LogicalDao<>(hooks, mapper, logicalDelete) :
                        new AbstractDao<>(hooks, mapper) {
                        });
    }

    private Optional<DaoHook<ENTITY>> getHooKOptional(LoadDaoHook hook) {
        Class<? extends DaoHook<ENTITY>> hookClass =
                (Class<? extends DaoHook<ENTITY>>) hook.value();
        return Optional.ofNullable(hookClass).map(i -> {
            try {
                if (hook.newInstance()) {
                    return instanceCache.computeIfAbsent(hookClass, j -> {
                        try {
                            return hookClass.getDeclaredConstructor().newInstance();
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            throw new IllegalArgumentException(e);
                        }
                    });
                } else {
                    return context.getBean(i);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Get " + i.getName() + " bean error", e);
            }
        });
    }
}
