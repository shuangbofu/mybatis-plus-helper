package io.github.shuangbofu.helper.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Set;

/**
 * Created by shuangbofu on 2021/9/18 6:07 下午
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({Set.class})
public class LongSetTypeHandler extends SetTypeHandler<Long> {
    @Override
    protected ListTypeHandler<Long> getListTypeHandler() {
        return new LongListTypeHandler();
    }
}
