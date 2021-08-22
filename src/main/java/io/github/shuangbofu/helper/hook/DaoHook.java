package io.github.shuangbofu.helper.hook;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

public interface DaoHook<ENTITY> {

    default void insert(ENTITY entity) {
    }

    default void defaultQuery(QueryWrapper<ENTITY> queryWrapper) {
    }

    default void defaultUpdate(UpdateWrapper<ENTITY> updateWrapper) {
    }
}
