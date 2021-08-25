package io.github.shuangbofu.helper.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.shuangbofu.helper.annotation.LogicalDelete;
import io.github.shuangbofu.helper.entity.IdEntity;
import io.github.shuangbofu.helper.handler.UpdateHandler;
import io.github.shuangbofu.helper.hook.DaoHook;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LogicalDao<ENTITY extends IdEntity, MAPPER extends BaseMapper<ENTITY>> extends AbstractDao<ENTITY, MAPPER> {

    private static Field sqlSet;

    static {
        try {
            sqlSet = UpdateWrapper.class.getDeclaredField("sqlSet");
            sqlSet.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private final String name;
    private final String valid;
    private final String inValid;

    public LogicalDao(List<DaoHook<ENTITY>> providers, MAPPER mapper, LogicalDelete logicalDelete) {
        super(providers, mapper);
        name = logicalDelete.name();
        valid = logicalDelete.valid();
        inValid = logicalDelete.inValid();

        daoHooks.add(new DaoHook<>() {
            @Override
            public void defaultQuery(QueryWrapper<ENTITY> queryWrapper) {
                queryWrapper.eq(name, valid);
            }

            @Override
            public void defaultUpdate(UpdateWrapper<ENTITY> updateWrapper) {
                updateWrapper.eq(name, valid);
            }
        });
    }

    @Override
    public int deleteBy(UpdateHandler<ENTITY> handler) {
        return updateBy(i -> {
            handler.accept(i);
            clearSqlSet(i);
            i.set(name, inValid);
        });
    }

    private void clearSqlSet(UpdateWrapper<ENTITY> uw) {
        try {
            sqlSet.set(uw, new ArrayList<>());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
