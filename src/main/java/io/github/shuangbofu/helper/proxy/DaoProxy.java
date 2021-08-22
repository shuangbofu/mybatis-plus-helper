package io.github.shuangbofu.helper.proxy;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.shuangbofu.helper.dao.AbstractDao;
import io.github.shuangbofu.helper.entity.IdEntity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DaoProxy<ENTITY extends IdEntity, MAPPER extends BaseMapper<ENTITY>> implements InvocationHandler {

    private final AbstractDao<ENTITY, MAPPER> dao;

    public DaoProxy(AbstractDao<ENTITY, MAPPER> dao) {
        this.dao = dao;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(dao, args);
    }
}
