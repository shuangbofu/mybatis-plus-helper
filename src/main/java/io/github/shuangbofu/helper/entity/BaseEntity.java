package io.github.shuangbofu.helper.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import io.github.shuangbofu.helper.annotation.LoadDaoHook;
import io.github.shuangbofu.helper.annotation.LogicalDelete;
import io.github.shuangbofu.helper.hook.SetTimeHook;

@LogicalDelete
@LoadDaoHook(value = SetTimeHook.class)
public abstract class BaseEntity extends IdEntity {

    @TableField(value = SetTimeHook.GMT_CREATE)
    private Long createTime;
    @TableField(value = SetTimeHook.GMT_MODIFIED)
    private Long updateTime;

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }
}
