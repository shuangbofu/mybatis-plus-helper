package io.github.shuangbofu.helper.handler;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import java.util.function.Consumer;

public interface UpdateHandler<ENTITY> extends Consumer<UpdateWrapper<ENTITY>> {
}
