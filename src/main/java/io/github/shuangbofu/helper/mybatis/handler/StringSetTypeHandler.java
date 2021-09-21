package io.github.shuangbofu.helper.mybatis.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.Set;

/**
 * Created by shuangbofu on 2021/9/18 6:06 下午
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes({Set.class})
public class StringSetTypeHandler extends SetTypeHandler<String> {
    @Override
    protected ListTypeHandler<String> getListTypeHandler() {
        return new StringListTypeHandler();
    }
}
