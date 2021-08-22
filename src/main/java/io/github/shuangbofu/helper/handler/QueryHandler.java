package io.github.shuangbofu.helper.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.function.Consumer;

public interface QueryHandler<ENTITY> extends Consumer<QueryWrapper<ENTITY>> {
}
