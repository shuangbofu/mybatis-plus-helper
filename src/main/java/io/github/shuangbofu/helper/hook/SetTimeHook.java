package io.github.shuangbofu.helper.hook;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.github.shuangbofu.helper.entity.BaseEntity;

public class SetTimeHook implements DaoHook<BaseEntity> {
    public static final String GMT_CREATE = "gmt_create";
    public static final String GMT_MODIFIED = "gmt_modified";

    public SetTimeHook() {
    }

    @Override
    public void insert(BaseEntity entity) {
        long now = System.currentTimeMillis();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
    }

    @Override
    public void defaultUpdate(UpdateWrapper<BaseEntity> updateWrapper) {
        updateWrapper.set(GMT_MODIFIED, System.currentTimeMillis());
    }

    @Override
    public void defaultQuery(QueryWrapper<BaseEntity> queryWrapper) {
    }
}
