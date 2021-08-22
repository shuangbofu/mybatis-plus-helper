package io.github.shuangbofu.helper.proxy;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.shuangbofu.helper.dao.BaseDao;
import io.github.shuangbofu.helper.entity.IdEntity;

import java.lang.reflect.Proxy;

public class DaoProxyFactory<ENTITY extends IdEntity, MAPPER extends BaseMapper<ENTITY>, DAO extends BaseDao<ENTITY, MAPPER>> {
    private final Class<DAO> daoInterface;

    public DaoProxyFactory(Class<DAO> daoInterface) {
        this.daoInterface = daoInterface;
    }

    public DAO newInstance(DaoProxy<ENTITY, MAPPER> daoProxy) {
        Object o = Proxy.newProxyInstance(daoInterface.getClassLoader(), new Class[]{daoInterface}, daoProxy);
        return (DAO) o;
    }
}
